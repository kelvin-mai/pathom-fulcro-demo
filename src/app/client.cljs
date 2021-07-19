(ns app.client
  (:require [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.inspect.inspect-client :as inspect]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [app.client.app :refer [APP]]
            [app.client.root :refer [Root]]))

(defn ^:after-load reload []
  (app/mount! APP Root "app")
  (comp/refresh-dynamic-queries! APP)
  (js/console.log "reloaded"))

(defn ^:export init []
  (log/info "application starting")
  (inspect/app-started! APP)
  (app/mount! APP Root "app"))
