(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl :refer [go halt reset reset-all]]
            [integrant.repl.state :as state]
            [nrepl.server]
            [cider.nrepl :refer [cider-nrepl-handler]]
            [app.api.system :as system]))

(ig-repl/set-prep!
 (fn []
   (system/read-config-file "resources/config/dev.edn")))

(defn start-dev
  "
  Entrypoint for clj -X:dev
  Start nrepl server with cider middleware and start system
  "
  [& _]
  (nrepl.server/start-server :port 1234
                             :bind "0.0.0.0"
                             :handler cider-nrepl-handler)
  (go))

(def db (:crux/db state/system))

(comment
  (go)
  (halt)
  (reset)
  (reset-all)

  state/system)
