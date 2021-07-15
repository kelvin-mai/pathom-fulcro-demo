(ns app.client.ui.products
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.algorithms.tempid :refer [tempid]]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.normalized-state :refer [swap!->]]
            [edn-query-language.core :as eql]))

(defmutation create-product
  [{:keys [id] :as params}]
  (action [{:keys [state ref]}]
          (swap!-> state
                   (assoc-in [:product/id id] params)
                   (update-in [:component/id :products :products] conj params)))
  (remote [env]
          (js/console.log `remote :env env)
          (eql/query->ast1 `[(mutation/create-product ~params)
                             [:transaction/success
                              :product/id]]))
  (ok-action [env]
             (js/console.log :create-product/ok-env env))
  (error-action [env]
                (js/console.log :create-product/error-env env)))

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
   (dom/button {:type "button"
                :onClick #(comp/transact! this
                                          `[(create-product
                                             ~{:product/id (tempid)
                                               :product/name "from ui"
                                               :product/price 20})])}
               "TEST")
   (map ui-product products)))

(def ui-products (comp/factory Products))
