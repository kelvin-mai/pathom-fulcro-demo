(ns app.api.system
  (:require [aero.core :as aero]
            [integrant.core :as ig]
            [org.httpkit.server :as http]
            [crux.api :as crux]
            [app.api.routes :refer [create-routes]]))

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

(defmethod ig/init-key :crux/db
  [_ {:keys [port jdbc]}]
  (println "Starting crux node")
  (crux/start-node 
    {:crux.jdbc/connection-pool {:dialect {:crux/module 'crux.jdbc.psql/->dialect}
                                 :db-spec jdbc}
     :crux/tx-log               {:crux/module     'crux.jdbc/->tx-log
                                 :connection-pool :crux.jdbc/connection-pool}
     :crux/document-store       {:crux/module 'crux.jdbc/->document-store
                                 :connection-pool :crux.jdbc/connection-pool}
     :crux.http-server/server   {:port port}}))

(defmethod ig/halt-key! :crux/db
  [_ node]
  (.close node))

(defmethod ig/init-key :app/routes
  [_ _]
  (println "Initializing application")
  (create-routes))
