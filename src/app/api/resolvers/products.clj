(ns app.api.resolvers.products
  (:require [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
            [app.utils.db :as db]
            [app.api.models.product :as product]))

(defresolver products-resolver
  [{:keys [db]} _]
  {::pc/output [{:products/all [:product/id]}]}
  {:products/all (db/get-all-idents db :product/id)})

(defresolver product-resolver
  [{:keys [db]} {:product/keys [id]}]
  {::pc/input #{:product/id}
   ::pc/output product/attrs}
  (db/get-entity db :product/id id))

(defmutation create-product-mutation
  [{:keys [db]} params]
  {::pc/sym 'create-product
   ::pc/params product/attrs
   ::pc/output [:transaction/success :product/id]}
  (let [{:product/keys [id] :as entity} (db/new-entity :product/id params)
        tx-status (db/submit! db [[:crux.tx/put entity]])]
    {:transaction/success tx-status
     :product/id id}))

(def resolvers [products-resolver
                product-resolver
                create-product-mutation])
