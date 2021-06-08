(ns app.api.routes
  (:require
   [reitit.ring :as ring]))

(def ping-routes
  ["/health_check" {:name ::ping
                    :get (fn [_]
                           {:status 200
                            :body {:ping "pong"}})}])

(def route-config
  {:data {:middleware []}})

(defn routes []
  (ring/ring-handler
   (ring/router
    [["/api"
      ping-routes]]
    route-config)
   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler
     {:not-found (constantly {:status 404
                              :body {:error "Route not found"}})}))))

