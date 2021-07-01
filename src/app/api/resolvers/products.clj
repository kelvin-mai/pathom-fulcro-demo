(ns app.api.resolvers.products
  (:require [taoensso.timbre :as log]
            [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
            [app.utils.db :as db]))

(def product-attrs
  [:product/name
   :product/price])

(defresolver products-resolver
  [{:keys [db]} _]
  {::pc/output [{:products [:product/id]}]}
  {:products [[:product/id 1]
              [:product/id 2]]})

(defresolver product-resolver 
  [{:keys [db]} {:product/keys [id] :as params}]
  {::pc/input #{:product/id}
   ::pc/output product-attrs}
  {:product/name "something"
   :product/price 250.2})

(defmutation create-product-mutation
  [{:keys [db]} _]
  {::pc/sym 'create-product
   ::pc/params product-attrs
   ::pc/output [:success :product/id]}
  {:success true
   :product/id 1})

(def resolvers [products-resolver
                product-resolver
                create-product-mutation])
