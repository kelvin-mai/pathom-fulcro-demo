(ns app.models.inventory.mutations
  (:require [com.wsscode.pathom.connect :as pc]
            [com.fulcrologic.fulcro.mutations :as m]
            #?(:clj [app.utils.db :as db])
            [app.utils.models :as models]
            [app.models.inventory :as inventory]))

#?(:clj
   (pc/defmutation create-mutation
     [{:keys [db]} {temp-id :inventory/id :as params}]
     {::pc/sym `create
      ::pc/params [:inventory/id :inventory/name :inventory/quantity :inventory/product]
      ::pc/output [:transaction/success :inventory/id]}
     (models/server-validation ::inventory/create params)
     (let [{:inventory/keys [id] :as entity} (db/new-entity :inventory params)
           tx-status (db/submit! db [[:crux.tx/put entity]])]
       {:transaction/success tx-status
        :inventory/id id
        :tempids {temp-id id}})))

#?(:clj
   (pc/defmutation update-quantity-mutation
     [{:keys [db]} {:inventory/keys [id quantity] :as params}]
     {::pc/sym `update-quantity
      ::pc/params [:inventory/id :inventory/quantity]
      ::pc/output [:transaction/success :inventory/id]}
     (models/server-validation ::inventory/update-quantity params)
     (let [{:inventory/keys [id] :as entity} (db/get-entity db :inventory id)
           entity (assoc entity :inventory/quantity quantity)
           tx-status (db/submit! db [[:crux.tx/put entity]])]
       {:transaction/success tx-status
        :inventory/id id})))

#?(:clj
   (def mutations [create-mutation
                   update-quantity-mutation]))
