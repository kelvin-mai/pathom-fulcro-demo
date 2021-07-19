(ns app.server.router
  (:require [taoensso.timbre :as log]
            [integrant.core :as ig]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [ring.middleware.cors :refer [wrap-cors]]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [com.fulcrologic.fulcro.server.api-middleware :as fulcro]))

(defn wrap-logging [handler]
  (fn [{:keys [uri request-method] :as request}]
    (log/info uri request-method)
    (handler request)))

(def global-route-config
  {:data {:muuntaja m/instance
          :middleware [[wrap-cors
                        :access-control-allow-origin [#"http://localhost:8000"]
                        :access-control-allow-methods [:get :post :put :delete :options]]
                       exception/exception-middleware
                       wrap-logging]}})

(def health-route
  ["/health-check"
   {:name ::health-check
    :get (fn [_]
           {:status 200
            :body {:ping "pong"}})}])

(def eql-middlewares
  [fulcro/wrap-transit-params
   fulcro/wrap-transit-response])

(defmethod ig/init-key :reitit/routes
  [_ {:keys [parser]}]
  (log/info "initializing reitit routes")
  (ring/ring-handler
   (ring/router
    [["/api"
      ["" {:name ::eql-api
           :post (fn [request]
                   (fulcro/handle-api-request
                    (:transit-params request)
                    (fn [tx]
                      (log/debug "tx" tx)
                      (parser {:request request} tx))))
           :middleware eql-middlewares}]
      ["" {:middleware [muuntaja/format-middleware]}
       health-route]]]
    global-route-config)
   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler
     {:not-found (constantly {:status 404
                              :headers {"content-type" "application/json"}
                              :body (m/encode "application/json" {:error "Route not found"})})}))))
