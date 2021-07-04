(ns app.client.app
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.networking.http-remote :as http]))

(defonce APP
  (app/fulcro-app
   {:remotes {:remote (http/fulcro-http-remote
                       {:url "http://localhost:6969/api"})}}))
