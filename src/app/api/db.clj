(ns app.api.db
  (:require [taoensso.timbre :as log]
            [integrant.core :as ig]
            [crux.api :as crux]))

(defmethod ig/init-key :crux/db
  [_ {:keys [port jdbc]}]
  (log/info "starting crux node")
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
