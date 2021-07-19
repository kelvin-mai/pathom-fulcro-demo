(ns app.server
  (:require [app.server.system :as system]))

(defn -main
  []
  (system/read-config-file "resources/config/prod.edn"))
