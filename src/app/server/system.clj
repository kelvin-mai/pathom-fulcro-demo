(ns app.server.system
  (:require [taoensso.timbre :as log]
            [aero.core :as aero]
            [integrant.core :as ig]
            app.server.http
            app.server.db
            app.server.parser
            app.server.router))

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defmethod ig/init-key :system/config
  [_ config]
  (log/info "system starting with config" config)
  config)

(defn read-config-file [profile]
  (aero/read-config
   "resources/config.edn"
   {:profile profile}))
