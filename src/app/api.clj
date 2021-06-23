(ns app.api
  (:require [app.api.system :as system]))

(defn -main
  []
  (system/read-config-file "resources/config/prod.edn"))
