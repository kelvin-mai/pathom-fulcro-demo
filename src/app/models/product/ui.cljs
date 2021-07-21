(ns app.models.product.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.algorithms.tempid :refer [tempid]]
            [app.models.product.mutations :as product.mutation]))

(defsc Product
  [this props]
  {:query [:product/id :product/name :product/price]
   :ident :product/id}
  (dom/div
   (pr-str props)))

(def ui-product (comp/factory Product {:keyfn :product/id}))

(defsc Products
  [this {:keys [products]}]
  {:query [{:products (comp/get-query Product)}]
   :ident (fn [] [:component/id :products])
   :initial-state {:products []}}
  (dom/div
   (dom/button :.px-4.py-1.m-2.bg-red-600.text-white.rounded
               {:type "button"
                :onClick #(comp/transact! this
                                          `[(product.mutation/create
                                             ~{:product/id (tempid)
                                               :product/name "from ui"
                                               :product/price 20})])}
               "TEST")
   (map ui-product products)))

(def ui-products (comp/factory Products))
