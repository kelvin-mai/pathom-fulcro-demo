(ns app.api.routes
  (:require [clojure.core.async :as async]
            [reitit.ring :as ring]
            [com.fulcrologic.fulcro.server.api-middleware :as fulcro-mw]
            [taoensso.timbre :as log]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]))

(def my-resolvers [])

(def parser
  (p/parallel-parser
   {::p/env {::p/reader                [p/map-reader
                                        pc/parallel-reader
                                        pc/open-ident-reader]
             ::pc/mutation-join-global [:tempids]}
    ::p/mutate pc/mutate-async
    ::p/plugins [(pc/connect-plugin {::pc/register my-resolvers})
                 (p/post-process-parser-plugin p/elide-not-found)
                 p/error-handler-plugin]}))

(def ping-routes
  ["/health" {:name ::ping
                    :get (fn [_]
                           (log/info "Route" ::ping)
                           {:status 200
                            :body {:ping "pong"}})}])

(def route-config
  {:data {:middleware [[fulcro-mw/wrap-api {:uri "/api"
                                            :parser (fn [q]
                                                      (async/<!! (parser {} q)))}]
                       fulcro-mw/wrap-transit-params
                       fulcro-mw/wrap-transit-response]}})

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

