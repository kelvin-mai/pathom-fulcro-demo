(ns app.client.app
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.networking.http-remote :as http]
            [app.models.product.ui :refer [Product]]
            [app.models.inventory.ui :refer [Inventory]]))

(defonce APP
  (app/fulcro-app
   {:remotes {:remote (http/fulcro-http-remote
                       {:url "http://localhost:6969/api"})}
    :client-did-mount
    (fn [app]
      (df/load! app :products/all
                Product
                {:target [:component/id :products :products]})
      (df/load! APP :inventory/all
                Inventory
                {:target [:component/id :inventories :inventories]}))
    ;
    }))
