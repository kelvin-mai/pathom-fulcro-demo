(ns app.server.db
  (:require [taoensso.timbre :as log]
            [integrant.core :as ig]
            [crux.api :as crux]))

(defmethod ig/init-key :crux/db
  [_ {:keys [config]}]
  (let [{:keys [crux-port jdbc]} config
        crux-config {:crux.jdbc/connection-pool {:dialect {:crux/module 'crux.jdbc.psql/->dialect}
                                                 :db-spec jdbc}
                     :crux/tx-log               {:crux/module     'crux.jdbc/->tx-log
                                                 :connection-pool :crux.jdbc/connection-pool}
                     :crux/document-store       {:crux/module 'crux.jdbc/->document-store
                                                 :connection-pool :crux.jdbc/connection-pool}}
        crux-config (when crux-port
                      (assoc crux-config
                             :crux.http-server/server {:port crux-port}))]
    (log/info "starting crux node")
    (when crux-port
      (log/info "crux http server enabled, port:" crux-port))
    (crux/start-node crux-config)))

(defmethod ig/halt-key! :crux/db
  [_ node]
  (.close node))
