(ns app.server.system
  (:require [aero.core :as aero]
            [integrant.core :as ig]
            app.server.http
            app.server.db
            app.server.parser
            app.server.router))

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defn read-config-file [file]
  (aero/read-config file))
