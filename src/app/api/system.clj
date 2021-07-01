(ns app.api.system
  (:require [aero.core :as aero]
            [integrant.core :as ig]
            app.api.server
            app.api.db
            app.api.parser
            app.api.router))

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defn read-config-file [file]
  (aero/read-config file))
