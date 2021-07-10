(ns app.client.ui.root
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [app.client.ui.products :refer [Products ui-products]]))

(defsc Root
  [this {:root/keys [message products]}]
  {:query [:root/message
           {:root/products (comp/get-query Products)}]
   :initial-state {:root/message "TODO"
                   :root/products {}}}
  (js/console.log products)
  (dom/div
   (dom/div message)
   (ui-products products)))

(def ui-root (comp/factory Root))
