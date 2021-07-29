(ns app.models.product.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.algorithms.tempid :refer [tempid]]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.mutations :as m]
            [app.models.product :as product]
            [app.models.product.mutations :as product.mutation]
            [app.client.routing :as r]
            [app.client.ui.headlessui :refer [headless-ui]]
            [app.client.ui.icons :refer [ui-icon]]
            [app.client.ui.forms :refer [ui-input]]
            [app.client.ui.table :as table]))

(defsc ProductForm
  [this {::product/keys [id name price] :as props}]
  {:query [::product/id ::product/name ::product/price fs/form-config-join]
   :ident ::product/id
   :form-fields #{::product/name ::product/price}
   :initial-state {::product/id :none
                   ::product/name ""
                   ::product/price 0}
   :pre-merge (fn [{:keys [data-tree]}]
                (fs/add-form-config ProductForm data-tree))}
  (let [onClose (comp/get-computed this :onClose)
        reset-form #(do
                      (comp/transact! this [(fs/clear-complete! props)])
                      (comp/transact! this `[(fs/reset-form!)]))
        reset-and-close #(do
                           (reset-form)
                           (onClose))]
    (dom/div :.bg-white.rounded.mx-auto.z-50
             (headless-ui
              :dialog-title
              {:classes [:.uppercase.text-center.font-bold.pt-2]}
              (if (uuid? id)
                (str "Update Product #" id)
                "Create Product"))
             (dom/form :.m-2
                       {:onSubmit (fn [e]
                                    (.preventDefault e)
                                    (let [submit-fn (if (uuid? id)
                                                      #(comp/transact! this
                                                                       `[(product.mutation/update!
                                                                          ~{::product/id id
                                                                            ::product/name name
                                                                            ::product/price price})])
                                                      #(comp/transact! this
                                                                       `[(product.mutation/create!
                                                                          ~{::product/id (tempid)
                                                                            ::product/name name
                                                                            ::product/price price})]))
                                          validity (fs/get-spec-validity props)
                                          dirty? (fs/dirty? props)]
                                      (if (and (= :valid validity)
                                               dirty?)
                                        (do
                                          (submit-fn)
                                          (onClose))
                                        (comp/transact! this [(fs/mark-complete! props)]))))}
                       (ui-input this ::product/name {:label "Product Name"
                                                      :onChange #(m/set-string! this ::product/name :event %)})
                       (ui-input this ::product/price {:label "Product Price"
                                                       :type "number"
                                                       :onChange #(m/set-double! this ::product/price :event %)})
                       (dom/div :.flex.justify-between
                                (dom/button :.border.px-8.py-1.rounded.font-bold.my-2.bg-red-600.text-white
                                            {:type "button"
                                             :onClick reset-and-close}
                                            "Cancel")
                                (dom/div
                                 (dom/button :.border.px-8.py-1.rounded.font-bold.my-2.bg-gray-600.text-white
                                             {:type "button"
                                              :onClick reset-form}
                                             "Reset")
                                 (dom/button :.border.px-8.py-1.rounded.font-bold.my-2.bg-blue-800.text-white
                                             {:type "submit"}
                                             "Submit")))))))

(def ui-product-form (comp/computed-factory ProductForm {:keyfn ::product/id}))

(defsc ProductFormPanel
  [this {:keys [product-form ui/open?]}]
  {:query [:ui/open?
           {:product-form (comp/get-query ProductForm)}]
   :ident (fn [] [:component/id :product-form-panel])
   :initial-state (fn [_] {:ui/open? true
                           :product-form (comp/get-initial-state ProductForm)})
   :initLocalState (fn [this _]
                     {:onClose #(m/set-value! this :ui/open? false)})}
  (let [onClose (comp/get-state this :onClose)]
    (headless-ui
     :dialog
     {:open open?
      :onClose onClose
      :classes [:.fixed.inset-0.z-10.overflow-y-auto]}
     (dom/div :.flex.items-center.justify-center.min-h-screen
              (headless-ui
               :dialog-overlay
               {:classes [:.fixed.inset-0.bg-black.opacity-80]})
              (dom/div :.bg-white.rounded.mx-auto.z-50
                       {:classes ["w-3/5"]}
                       (ui-product-form (comp/computed product-form {:onClose onClose})))))))

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
                                                `[(product.mutation/set-edit-form
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
                   :product-form-panel {}}
   :route-segment ["products"]
   ::r/route [^:alias ["/" {:name :default :segment ["products"]}]
              ["/products" {:name :products :segment ["products"]}]]
   :will-enter (fn [app _]
                 (dr/route-deferred
                  [:component/id :products]
                  (fn []
                    (df/load! app ::product/all
                              Product
                              {:target [:component/id :products :products]
                               :post-mutation `dr/target-ready
                               :post-mutation-params {:target [:component/id :products]}}))))}
  (dom/div
   (ui-product-form-panel product-form-panel)
   (dom/button :.px-4.py-1.m-2.bg-red-600.text-white.rounded
               {:type "button"
                :onClick #(comp/transact! this
                                          `[(product.mutation/create!
                                             ~{::product/id (tempid)
                                               ::product/name "from ui"
                                               ::product/price 20.45})])}
               "TEST")
   (dom/button :.border.px-8.py-1.rounded.font-bold.my-2.bg-blue-600.text-white
               {:type "button"
                :onClick #(comp/transact! this `[(product.mutation/set-edit-form)])}
               "New")
   (table/ui-table {:heads ["ID" "Name" "Price" nil]
                    :classes ["m-4"]}
                   (map ui-product products))))

(def ui-products (comp/factory Products))
