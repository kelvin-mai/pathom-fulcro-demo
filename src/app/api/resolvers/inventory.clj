(ns app.api.resolvers.inventory
  (:require [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
            [app.utils.db :as db]
            [app.api.models.inventory :as inventory]))

(defresolver all-inventory-resolver
  [{:keys [db]} _]
  {::pc/output [{:inventory/all [:inventory/id]}]}
  {:inventory/all (db/get-all-idents db :inventory/id)})

(defresolver inventory-resolver
  [{:keys [db]} {:inventory/keys [id]}]
  {::pc/input #{:inventory/id}
   ::pc/output inventory/attrs}
  (db/get-entity db :inventory/id id))

(defresolver inventory-history-resolver
  [{:keys [db]} {:inventory/keys [id]}]
  {::pc/input #{:inventory/id}
   ::pc/output [:inventory/history]}
  (let [history (db/entity-history db id)
        history->data (fn [snapshot]
                        {:snapshot/time (:crux.db/valid-time snapshot)
                         :snapshot/quantity (get-in snapshot [:crux.db/doc :inventory/quantity])})]
    {:inventory/history (map history->data history)}))

(defmutation create-inventory-mutation
  [{:keys [db]} params]
  {::pc/sym `mutation/create-inventory
   ::pc/params inventory/attrs
   ::pc/output [:transaction/success :inventory/id]}
  (let [{:inventory/keys [id] :as entity} (db/new-entity :inventory/id params)
        tx-status (db/submit! db [[:crux.tx/put entity]])]
    {:transaction/success tx-status
     :inventory/id id}))

(defmutation update-inventory-quantity-mutation
  [{:keys [db]} {:inventory/keys [id quantity]}]
  {::pc/sym `mutation/update-inventory-quantity
   ::pc/params [:inventory/id :inventory/quantity]
   ::pc/output [:transaction/success :inventory/id]}
  (let [{:inventory/keys [id] :as entity} (db/get-entity db :inventory/id id)
        entity (assoc entity :inventory/quantity quantity)
        tx-status (db/submit! db [[:crux.tx/put entity]])]
    {:transaction/success tx-status
     :inventory/id id}))

(def resolvers [all-inventory-resolver
                inventory-resolver
                inventory-history-resolver
                create-inventory-mutation
                update-inventory-quantity-mutation])
