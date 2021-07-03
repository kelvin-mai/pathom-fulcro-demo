(ns app.api.test-utils
  (:require [integrant.core :as ig]
            [app.api.system :as system]))

(def test-system (atom nil))

(defn use-system
  []
  (fn [test-fn]
    (reset! test-system
            (let [ig-config (system/read-config-file "resources/config/dev.edn")]
              (ig/init ig-config)))
    (test-fn)
    (ig/halt! @test-system)
    (reset! test-system nil)))
