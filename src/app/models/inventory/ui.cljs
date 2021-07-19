(ns app.models.inventory.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

(defsc Inventory
  [this props]
  {:query [:inventory/id :inventory/name :inventory/quantity]
   :ident :inventory/id}
  (dom/div
   (pr-str props)))

(def ui-inventory (comp/factory Inventory {:keyfn :inventory/id}))

(defsc Inventories
  [this {:keys [inventories]}]
  {:query [{:inventories (comp/get-query Inventory)}]
   :ident (fn [] [:component/id :inventories])
   :initial-state {:inventories []}}
  (dom/div
   (map ui-inventory inventories)))

(def ui-inventories (comp/factory Inventories))
