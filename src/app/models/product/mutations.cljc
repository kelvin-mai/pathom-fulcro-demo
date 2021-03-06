(ns app.models.product.mutations
  (:require [com.wsscode.pathom.connect :as pc]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.algorithms.normalized-state :refer [swap!->]]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            #?(:clj [app.utils.db :as db])
            [app.utils.models :as models]
            [app.models.product :as product]))

#?(:clj
   (pc/defmutation create!
     [{:keys [db]} {temp-id ::product/id :as params}]
     {::pc/sym `create!
      ::pc/params [::product/name ::product/price]
      ::pc/output [:transaction/success ::product/id]}
     (models/server-validation ::product/create params)
     (let [{::product/keys [id] :as entity} (db/new-entity ::product/id params)
           tx-status (db/submit! db [[:crux.tx/put entity]])]
       {:transaction/success tx-status
        :tempids {temp-id id}
        ::product/id id}))

   :cljs
   (m/defmutation create!
     [{::product/keys [id] :as params}]
     (action [{:keys [state]}]
             (swap!-> state
                      (assoc-in [::product/id id] params)
                      (update-in [:component/id :products :products] conj [::product/id id])))
     (remote [env] true)))

#?(:clj
   (pc/defmutation update!
     [{:keys [db]} {::product/keys [id] :as params}]
     {::pc/sym `update!
      ::pc/params [::product/id ::product/name ::product/price]
      ::pc/output [:transaction/success ::product/id]}
     (models/server-validation ::product/update params)
     (let [prev (db/get-entity db ::product/id id)
           entity (if prev
                    (assoc params :crux.db/id id)
                    (models/server-not-found))
           tx-status (db/submit! db [[:crux.tx/put entity]])]
       {:transaction/success tx-status
        ::product/id id}))
   :cljs
   (m/defmutation update!
     [{::product/keys [id]}]
     (action [{:keys [state]}]
             (swap! state fs/entity->pristine* [::product/id id])
             state)
     (remote [env] false)))

#?(:clj
   (pc/defmutation delete!
     [{:keys [db]} {::product/keys [id]}]
     {::pc/sym `delete!
      ::pc/params [::product/id]
      ::pc/output [:transaction/success ::product/id]}
     (let [prev (db/get-entity db ::product/id id)
           entity (when (nil? prev)
                    (models/server-not-found))
           tx-status (db/submit! db [[:crux.tx/delete id]])]
       {:transaction/success tx-status
        ::product/id id}))
   :cljs
   (m/defmutation delete!
     [{::product/keys [id] :as params}]
     (action [{:keys [state]}]
             (swap!-> state
                      (update ::product/id dissoc id)
                      (update-in [:component/id :products :products] models/remove-from-idents id)))
     (remote [env] true)))

#?(:cljs
   (m/defmutation set-edit-form
     [{::product/keys [id]}]
     (action [{:keys [state]}]
             (let [id (or id :none)
                   ident [::product/id id]
                   product-form-class (comp/registry-key->class :app.models.product.ui/ProductForm)
                   product-form-panel-class (comp/registry-key->class :app.models.product.ui/ProductFormPanel)]
               (swap!-> state
                        (merge/merge-component product-form-panel-class {:product-form ident
                                                                         :ui/open? true})
                        (fs/add-form-config* product-form-class ident))))))

#?(:clj
   (def mutations [create!
                   update!
                   delete!]))
