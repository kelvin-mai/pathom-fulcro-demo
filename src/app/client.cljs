(ns app.client)

(defn ^:after-load mount []
  (js/console.log "reloaded"))

(defn ^:export init []
  (js/console.log "starting")
  (mount))
