(ns app.api.routes
  (:require [reitit.ring :as ring]
            [muuntaja.core :as m]
            [app.api.routes.json :refer [json-routes]]
            [app.api.routes.eql :refer [eql-route]]))

(def global-route-config
  {:data {:muuntaja m/instance
          :middleware []}})

(defn create-routes []
  (ring/ring-handler
   (ring/router
    [["/api"
      json-routes
      eql-route]]
    global-route-config)
   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler
     {:not-found (constantly {:status 404
                              :body {:error "Route not found"}})}))))

