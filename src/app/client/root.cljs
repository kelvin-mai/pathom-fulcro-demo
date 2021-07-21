(ns app.client.root
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [app.models.product.ui :refer [Products ui-products]]))

(defsc Root
  [this {:root/keys [message products]}]
  {:query [:root/message
           {:root/products (comp/get-query Products)}]
   :initial-state {:root/message "TODO"
                   :root/products {}}}
  (dom/div
   (dom/div message)
   (dom/h3 "All Products")
   (ui-products products)))

(def ui-root (comp/factory Root))
