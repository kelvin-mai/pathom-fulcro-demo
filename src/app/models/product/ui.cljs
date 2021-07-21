(ns app.models.product.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.algorithms.tempid :refer [tempid]]
            [app.models.product :as product]
            [app.models.product.mutations :as product.mutation]
            [app.client.ui.icons :refer [ui-icon]]
            [app.client.ui.table :as table]))

(defsc Product
  [this {::product/keys [id name price]}]
  {:query [::product/id ::product/name ::product/price]
   :ident ::product/id}
  (dom/tr {:classes [table/table-row]}
          (dom/td {:classes [table/table-cell]}
                  (str id))
          (dom/td {:classes [table/table-cell]}
                  name)
          (dom/td {:classes [table/table-cell]}
                  price)
          (dom/td {:classes [table/table-cell]}
                  (dom/button :.text-gray-500.mr-4
                              {:onClick
                               #(js/console.log this
                                                `[(set-form
                                                   ~{::product/id id})])}
                              (ui-icon :edit))
                  (dom/button :.text-red-400
                              {:onClick
                               #(comp/transact! this
                                                `[(product.mutation/delete!
                                                   ~{::product/id id})])}
                              (ui-icon :delete)))))

(def ui-product (comp/factory Product {:keyfn ::product/id}))

(defsc Products
  [this {:keys [products]}]
  {:query [{:products (comp/get-query Product)}]
   :ident (fn [] [:component/id :products])
   :initial-state {:products []}}
  (dom/div
   (dom/button :.px-4.py-1.m-2.bg-red-600.text-white.rounded
               {:type "button"
                :onClick #(comp/transact! this
                                          `[(product.mutation/create!
                                             ~{::product/id (tempid)
                                               ::product/name "from ui"
                                               ::product/price 20})])}
               "TEST")
   (table/ui-table {:heads ["ID" "Name" "Price" nil]
                    :classes ["m-4"]}
                   (map ui-product products))))

(def ui-products (comp/factory Products))
