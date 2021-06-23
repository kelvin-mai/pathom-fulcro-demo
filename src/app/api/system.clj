(ns app.api.system
  (:require [aero.core :as aero]
            [integrant.core :as ig]
            [org.httpkit.server :as http]
            [app.api.routes :refer [routes]]))

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defn read-config-file [file]
  (aero/read-config file))

(defmethod ig/init-key :http/server
  [_ {:keys [handler port]}]
  (println (str "Server started on port: " port))
  (http/run-server handler {:port port}))

(defmethod ig/halt-key! :http/server
  [_ server]
  (println "Server stopping")
  (server :timeout 100))

(defmethod ig/init-key :app/routes
  [_ _]
  (println "Initializing application")
  (routes))
