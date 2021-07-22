(ns app.models.product.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.algorithms.tempid :refer [tempid]]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.normalized-state :refer [swap!->]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [app.models.product :as product]
            [app.models.product.mutations :as product.mutation]
            [app.client.ui.icons :refer [ui-icon]]
            [app.client.ui.forms :refer [ui-input]]
            [app.client.ui.table :as table]
            [com.fulcrologic.fulcro.mutations :as m]))

(defsc ProductForm
  [this {::product/keys [id name price] :as props}]
  {:query [::product/id ::product/name ::product/price fs/form-config-join]
   :ident ::product/id
   :form-fields #{::product/name ::product/price}
   :initial-state {::product/id :none
                   ::product/name ""
                   ::product/price 0}}
  (dom/form :.m-2
            {:onSubmit (fn [e]
                         (.preventDefault e)
                         (if (uuid? id)
                           (comp/transact! this
                                           `[(product.mutation/update!
                                              ~{::product/id id
                                                ::product/name name
                                                ::product/price price})])
                           (comp/transact! this
                                           `[(product.mutation/create!
                                              ~{::product/id (tempid)
                                                ::product/name name
                                                ::product/price price})])))}
            (ui-input this ::product/name {:label "Product Name"
                                           :onChange #(m/set-string! this ::product/name :event %)})
            (ui-input this ::product/price {:label "Product Price"
                                            :type "number"
                                            :onChange #(m/set-double! this ::product/price :event %)})
            (dom/button :.border.px-8.py-1.rounded.font-bold.my-2.bg-red-400.text-white
                        {:type "button"
                         :onClick #(comp/transact! this `[(fs/reset-form!)])}
                        "Reset")
            (dom/button :.border.px-8.py-1.rounded.font-bold.my-2.bg-blue-600.text-white
                        {:type "button"
                         :onClick #(comp/transact! this `[(set-edit-form)])}
                        "New")
            (dom/button :.border.px-8.py-1.rounded.font-bold.my-2.bg-gray-800.text-white
                        {:type "submit"}
                        "Submit")))

(def ui-product-form (comp/factory ProductForm {:keyfn ::product/id}))

(defsc ProductFormPanel
  [this {:keys [product-form] :as props}]
  {:query [{:product-form (comp/get-query ProductForm)}]
   :ident (fn [] [:component/id :product-form-panel])
   :initial-state (fn [_] {:product-form (comp/get-initial-state ProductForm)})}
  (ui-product-form product-form))

(def ui-product-form-panel (comp/factory ProductFormPanel))

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
                               #(comp/transact! this
                                                `[(set-edit-form
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
  [this {:keys [products product-form-panel]}]
  {:query [{:products (comp/get-query Product)}
           {:product-form-panel (comp/get-query ProductFormPanel)}]
   :ident (fn [] [:component/id :products])
   :initial-state {:products []
                   :product-form-panel {}}}
  (dom/div
   (ui-product-form-panel product-form-panel)
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

(m/defmutation set-edit-form
  [{::product/keys [id] :as params}]
  (action [{:keys [state]}]
          (let [id (or id :none)
                empty-product {::product/id :none
                               ::product/name ""
                               ::product/price 0}
                init-create-form (fn [s]
                                   (if (= id :none)
                                     (assoc-in s [::product/id :none] empty-product)
                                     s))]
            (swap!-> state
                     (assoc-in [:component/id :product-form-panel :product-form] [::product/id id])
                     (init-create-form)
                     (fs/add-form-config* ProductForm [::product/id id])))))
