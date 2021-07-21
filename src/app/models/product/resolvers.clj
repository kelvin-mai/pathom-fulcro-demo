(ns app.models.product.resolvers
  (:require [com.wsscode.pathom.connect :as pc :refer [defresolver]]
            [app.utils.db :as db]
            [app.models.product :as product]))

(defresolver products-resolver
  [{:keys [db]} _]
  {::pc/output [{::product/all [::product/id]}]}
  {::product/all (db/get-all-idents db ::product/id)})

(defresolver product-resolver
  [{:keys [db]} {::product/keys [id]}]
  {::pc/input #{::product/id}
   ::pc/output [::product/name ::product/price]}
  (db/get-entity db ::product/id id))

(def resolvers [products-resolver
                product-resolver])
