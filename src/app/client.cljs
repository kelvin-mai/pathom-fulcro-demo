(ns app.client
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [app.client.app :refer [APP]]
            [app.client.ui.root :refer [Root]]))

(defn ^:after-load reload []
  (app/mount! APP Root "app")
  (comp/refresh-dynamic-queries! APP)
  (js/console.log "reloaded"))

(defn ^:export init []
  (app/mount! APP Root "app")
  (js/console.log "starting"))
