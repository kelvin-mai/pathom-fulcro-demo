(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl :refer [go halt reset reset-all]]
            [integrant.repl.state :as state]
            [nrepl.server]
            [cider.nrepl :refer [cider-nrepl-handler]]
            [app.api.system :as system]))

(ig-repl/set-prep!
 (fn []
   (system/read-config-file "resources/config/dev.edn")))

(defn start-dev
  "
  Entrypoint for clj -X:dev
  Start nrepl server with cider middleware and start system
  "
  [& _]
  (nrepl.server/start-server :port 1234
                             :bind "0.0.0.0"
                             :handler cider-nrepl-handler)
  (go))

(comment
  (go)
  (halt)
  (reset)
  (reset-all)

  state/system
  ;; expose system components for repl use
  (def db (:crux/db state/system))
  (def parser (:pathom/parser state/system))

  (parser {:db db}
          [{[:product/id 2]
            [:product/id
             :product/name
             :product/price]}])

  (parser {:db db}
          #_[{:products [:product/id]}]
          [{:products {:product/id [:product/id
                                    :product/name
                                    :product/price]}}]
          )

  (parser {:db db}
          '[{(create-product {:product/name "new product"
                               :product/price 22.5}) 
             [:success
              :product/id]}])
  ;
  )
