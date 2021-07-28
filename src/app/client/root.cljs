(ns app.client.root
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
            [com.fulcrologic.fulcro.dom :as dom]
            [app.models.product.ui :refer [Products]]
            [app.models.inventory.ui :refer [Inventories]]))

(defrouter RootRouter [this props]
  {:router-targets [Products Inventories]}
  (js/console.log props)
  (dom/div "Loading..."))

(def ui-root-router (comp/factory RootRouter))

(defsc Root
  [this {:root/keys [router]}]
  {:query [{:root/router (comp/get-query RootRouter)}]
   :initial-state {:root/router {}}}
  (ui-root-router router))

(def ui-root (comp/factory Root))

