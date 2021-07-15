(ns app.client.ui.root
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [app.client.ui.products :refer [Products ui-products]]
            [app.client.ui.inventory :refer [Inventories ui-inventories]]))

(defsc Root
  [this {:root/keys [message products inventories]}]
  {:query [:root/message
           {:root/products (comp/get-query Products)}
           {:root/inventories (comp/get-query Inventories)}]
   :initial-state {:root/message "TODO"
                   :root/products {}
                   :root/inventories {}}}
  (dom/div
   (dom/div message)
   (dom/h3 "All Products")
   (ui-products products)
   (dom/h3 "All Inventories")
   (ui-inventories inventories)))

(def ui-root (comp/factory Root))
