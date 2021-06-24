(ns app.api.routes.eql
  (:require [clojure.core.async :as async]
            [com.fulcrologic.fulcro.server.api-middleware :as fulcro]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]))

(def my-resolvers [])

(def parser
  (p/parallel-parser
   {::p/env {::p/reader                [p/map-reader
                                        pc/parallel-reader
                                        pc/open-ident-reader]
             ::pc/mutation-join-global [:tempids]}
    ::p/mutate pc/mutate-async
    ::p/plugins [(pc/connect-plugin {::pc/register my-resolvers})
                 (p/post-process-parser-plugin p/elide-not-found)
                 p/error-handler-plugin]}))

(def eql-middlewares
  [[fulcro/wrap-api {:uri "/api"
                     :parser (fn [q]
                               (async/<!! (parser {} q)))}]
   fulcro/wrap-transit-params
   fulcro/wrap-transit-response])

(def eql-route
  ["" {:middleware eql-middlewares}])
