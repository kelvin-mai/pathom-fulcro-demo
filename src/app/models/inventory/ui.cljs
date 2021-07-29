(ns app.models.inventory.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [app.client.routing :as r]
            [app.models.inventory :as inventory]))

(defsc Inventory
  [this props]
  {:query [::inventory/id ::inventory/name ::inventory/quantity]
   :ident ::inventory/id}
  (dom/div
   (pr-str props)))

(def ui-inventory (comp/factory Inventory {:keyfn ::inventory/id}))

(defsc Inventories
  [this {:keys [inventories]}]
  {:query [{:inventories (comp/get-query Inventory)}]
   :ident (fn [] [:component/id :inventories])
   :initial-state {:inventories []}
   :route-segment ["inventory"]
   ::r/route ["/inventory" {:name :inventory :segment ["inventory"]}]
   :will-enter (fn [app _]
                 (dr/route-deferred
                  [:component/id :inventories]
                  (fn []
                    (df/load! app ::inventory/all
                              Inventory
                              {:target [:component/id :inventories :inventories]
                               :post-mutation `dr/target-ready
                               :post-mutation-params {:target [:component/id :inventories]}}))))}
  (dom/div
   (dom/h1 "TODO")
   (map ui-inventory inventories)))

(def ui-inventories (comp/factory Inventories))
