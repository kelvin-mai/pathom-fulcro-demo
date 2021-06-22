(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl :refer [go halt reset reset-all]]
            [integrant.repl.state :as state]
            [nrepl.server]
            [cider.nrepl :refer [cider-nrepl-handler]]
            [app.system :as system]))

(ig-repl/set-prep!
 (fn []
   (system/read-config-file "resources/config.edn")))

(defn start-dev 
  "
  Entrypoint for clj -X:dev
  Start nrepl server with cider middleware and start system
  WARNING: Do not reload user namespace
  "
  [& _]
  (nrepl.server/start-server :port 12345
                             :bind "0.0.0.0"
                             :handler cider-nrepl-handler)
  (go))

(comment
  (go)
  (halt)
  (reset)
  (reset-all)

  state/system)
