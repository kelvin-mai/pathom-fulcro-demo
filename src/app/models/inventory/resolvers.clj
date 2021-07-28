(ns app.models.inventory.resolvers
  (:require [com.wsscode.pathom.connect :as pc :refer [defresolver]]
            [app.utils.db :as db :refer [id->ident]]
            [app.models.product :as product]
            [app.models.inventory :as inventory]))

(defresolver all-inventory-resolver
  [{:keys [db]} _]
  {::pc/output [{::inventory/all [::inventory/id]}]}
  {::inventory/all (db/get-all-idents db ::inventory/id)})

(defresolver inventory-resolver
  [{:keys [db]} {::inventory/keys [id]}]
  {::pc/input #{::inventory/id}
   ::pc/output [::inventory/name ::inventory/quantity ::inventory/product]}
  (let [entity (db/get-entity db ::inventory/id id)]
    (update entity ::inventory/product id->ident ::product/id)))

(defresolver inventory-history-resolver
  [{:keys [db]} {::inventory/keys [id]}]
  {::pc/input #{::inventory/id}
   ::pc/output [::inventory/history]}
  (let [history (db/entity-history db id)
        history->data (fn [snapshot]
                        {:time (:crux.db/valid-time snapshot)
                         :quantity (get-in snapshot [:crux.db/doc ::inventory/quantity])})]
    {::inventory/history (map history->data history)}))

(def resolvers [all-inventory-resolver
                inventory-resolver
                inventory-history-resolver])
