(ns app.api.routes.json
  (:require [taoensso.timbre :as log]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]))

(def json-middlewares
  [muuntaja/format-middleware
   exception/exception-middleware])

(def ping-routes
  ["/health" {:name ::ping
              :get (fn [_]
                     (log/info "Route" ::ping)
                     {:status 200
                      :body {:ping "pong"}})}])

(def json-routes
  ["" {:middleware json-middlewares}
   ping-routes])
