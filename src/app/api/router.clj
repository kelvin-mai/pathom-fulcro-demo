(ns app.api.router
  (:require [taoensso.timbre :as log]
            [integrant.core :as ig]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [com.fulcrologic.fulcro.server.api-middleware :as fulcro]))

(def global-route-config
  {:data {:muuntaja m/instance
          :middleware []}})

(def json-middlewares
  [muuntaja/format-middleware
   exception/exception-middleware])

(def health-route
  ["/health-check"
   {:name ::health-check
    :get (fn [_]
           (log/info "route" ::health-check)
           {:status 200
            :body {:ping "pong"}})}])

(defn create-eql-middlewares [parser]
  [[fulcro/wrap-api {:uri "/api"
                     :parser parser}]
   fulcro/wrap-transit-params
   fulcro/wrap-transit-response])

(defmethod ig/init-key :reitit/routes
  [_ {:keys [parser]}]
  (log/info "initializing reitit routes")
  (ring/ring-handler
   (ring/router
    [["/api"
      ["" {:middleware json-middlewares}
       health-route]
      ["" {:middleware (create-eql-middlewares parser)}]]]
    global-route-config)
   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler
     {:not-found (constantly {:status 404
                              :body {:error "Route not found"}})}))))
