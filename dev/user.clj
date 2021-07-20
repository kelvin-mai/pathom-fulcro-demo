(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [integrant.core :as ig]
            [integrant.repl :as ig-repl :refer [go halt]]
            [integrant.repl.state :as state]
            [nrepl.server]
            [cider.nrepl :refer [cider-nrepl-handler]]
            [app.server.system :as system]))

(ig-repl/set-prep!
 (fn []
   (system/read-config-file {:profile :dev})))

(declare db parser)

(defn start-interactive
  "Start and expose system components for repl use"
  []
  (go)
  (def db (:crux/db state/system))
  (def parser (:pathom/parser state/system))
  ::ready!)

(defn start-dev
  "
  Entrypoint for clj -X:dev
  Start nrepl server with cider middleware and start system
  "
  [& _]
  (nrepl.server/start-server :port 1234
                             :bind "0.0.0.0"
                             :handler cider-nrepl-handler)
  (start-interactive))

(defn restart
  "Stop system, reload code, and restart in interactive mode"
  []
  (halt)
  (tools-ns/refresh :after 'user/start-interactive))

(comment
  (halt)
  (restart)
  state/system
  ;
  )
