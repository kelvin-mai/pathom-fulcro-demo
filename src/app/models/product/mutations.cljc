(ns app.models.product.mutations
  (:require [com.wsscode.pathom.connect :as pc]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.algorithms.normalized-state :refer [swap!->]]
            #?(:clj [app.utils.db :as db])
            [app.utils.models :as models]
            [app.models.product :as product]))

#?(:clj
   (pc/defmutation create
     [{:keys [db]} {temp-id :product/id :as params}]
     {::pc/sym `create
      ::pc/params [:product/name :product/price]
      ::pc/output [:transaction/success :product/id]}
     (models/server-validation ::product/create params)
     (let [{:product/keys [id] :as entity} (db/new-entity :product params)
           tx-status (db/submit! db [[:crux.tx/put entity]])]
       {:transaction/success tx-status
        :product/id id
        :tempids {temp-id id}}))

   :cljs
   (m/defmutation create
     [{:keys [id] :as params}]
     (action [{:keys [state ref]}]
             (swap!-> state
                      (assoc-in [:product/id id] params)
                      (update-in [:component/id :products :products] conj params)))
     (remote [env] true)))

#?(:clj
   (def mutations [create]))
