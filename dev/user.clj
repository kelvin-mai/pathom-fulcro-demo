(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl :refer [go halt reset reset-all]]
            [integrant.repl.state :as state]
            [app.system :as system]))

(ig-repl/set-prep!
 (fn []
   (system/read-config-file "resources/config.edn")))

(comment
  (go)
  (halt)
  (reset)
  (reset-all)

  state/system)
