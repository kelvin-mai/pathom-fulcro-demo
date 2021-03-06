(ns app.server.http
  (:require [taoensso.timbre :as log]
            [integrant.core :as ig]
            [org.httpkit.server :as http]))

(defmethod ig/init-key :http/server
  [_ {:keys [handler config]}]
  (let [port (:http-port config)]
    (log/info (str "server started on port: " port))
    (http/run-server handler {:port port})))

(defmethod ig/halt-key! :http/server
  [_ server]
  (log/info "server stopping")
  (server :timeout 100))
