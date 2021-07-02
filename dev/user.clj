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

;; expose system components for repl use
(def db (:crux/db state/system))
(def parser (:pathom/parser state/system))

(comment
  (go)
  (halt)
  (reset)
  (reset-all)

  state/system

  (app.utils.db/get-all-idents db :product/id)
  (app.utils.db/get-entity db :product/id #uuid "0a2d1fc5-de79-4c3d-9ab7-781c0bc27dec")
  (app.utils.db/entity db #uuid "0a2d1fc5-de79-4c3d-9ab7-781c0bc27dec")
  (crux.api/submit-tx db
                      [[:crux.tx/put
                        {:crux.db/id 1
                         :product/id 1
                         :product/name "new product"
                         :product/price 22.5}]])

  (parser {:db db}
          [:products/all])

  (parser {:db db}
          [{[:product/id #uuid "0a2d1fc5-de79-4c3d-9ab7-781c0bc27dec"]
            [:product/id
             :product/name
             :product/price]}])

  (parser {:db db}
          [{:products/all [:product/id
                           :product/name
                           :product/price]}])

  (parser {:db db}
          '[{(create-product {:product/name "new product"
                              :product/price 22.5})
             [:transaction/success
              :product/id
              :product/name
              :product/price]}])
  ;
  )
