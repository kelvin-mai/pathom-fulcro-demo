(ns app.api.resolvers.products
  (:require [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
            [app.utils.db :as db]
            [app.utils.models :as models]
            [app.models.product :as product]))

(defresolver products-resolver
  [{:keys [db]} _]
  {::pc/output [{:products/all [:product/id]}]}
  {:products/all (db/get-all-idents db :product)})

(defresolver product-resolver
  [{:keys [db]} {:product/keys [id]}]
  {::pc/input #{:product/id}
   ::pc/output [:product/name :product/price]}
  (let [entity (db/get-entity db :product id)]
    (models/resolve-model :product entity)))

(defmutation create-product-mutation
  [{:keys [db]} {temp-id :product/id :as params}]
  {::pc/sym `mutation/create-product
   ::pc/params [:product/name :product/price]
   ::pc/output [:transaction/success :product/id]}
  (models/validate ::product/create params)
  (let [{:product/keys [id] :as entity} (db/new-entity :product params)
        tx-status (db/submit! db [[:crux.tx/put entity]])]
    {:transaction/success tx-status
     :product/id id
     :tempids {temp-id id}}))

(def resolvers [products-resolver
                product-resolver
                create-product-mutation])
