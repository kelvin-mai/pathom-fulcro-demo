(ns app.api.parser
  (:require [taoensso.timbre :as log]
            [integrant.core :as ig]
            [clojure.core.async :refer [<!!]]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [com.wsscode.pathom.viz.ws-connector.core :as p.viz]
            [app.api.resolvers.products :as products]))

(def registry [(pc/constantly-resolver :ok "OK")
               products/resolvers])

(defn preprocess-parser-plugin
  [f]
  {::p/wrap-parser
   (fn [parser]
     (fn [env tx]
       (let [{:keys [env tx] :as req} (f {:env env :tx tx})]
         (if (and (map? env) (seq tx))
           (parser env tx)
           {}))))})

(defn log-requests [{:keys [env tx] :as req}]
  (log/debug "pathom transaction" (pr-str tx))
  req)

(defn create-parser [db]
  (p/parallel-parser
   {::p/env {::p/reader                [p/map-reader
                                        pc/reader2
                                        pc/parallel-reader
                                        pc/open-ident-reader
                                        p/env-placeholder-reader]
             ::p/placeholder-prefixes #{">"}}
    ::p/mutate pc/mutate-async
    ::p/plugins [(pc/connect-plugin {::pc/register registry})
                 (p/env-wrap-plugin (fn [env]
                                      (assoc env
                                             :db db)))
                 (preprocess-parser-plugin log-requests)
                 p/error-handler-plugin
                 (p/post-process-parser-plugin p/elide-not-found)]}))

(defn wrap-parser [parser]
  (fn [env tx]
    (<!! (parser env tx))))

(defmethod ig/init-key :pathom/parser
  [_ {:keys [viz-enabled? parser-id db]}]
  (log/info "configuring pathom parser")
  (cond->> (create-parser db)
    viz-enabled? (p.viz/connect-parser
                  {::p.viz/parser-id parser-id})
    true (wrap-parser)))
