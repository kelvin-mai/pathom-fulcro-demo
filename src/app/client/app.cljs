(ns app.client.app
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.networking.http-remote :as http]
            [app.models.product :as product]
            [app.models.product.ui :refer [Product]]
            [app.models.inventory.ui :refer [Inventory]]))

(defonce APP
  (app/fulcro-app
   {:remotes {:remote (http/fulcro-http-remote
                       {:url "http://localhost:6969/api"})}}))

(comment
  (require '[com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]])
  (dr/change-route! APP (dr/path-to app.models.inventory.ui/Inventories)))
