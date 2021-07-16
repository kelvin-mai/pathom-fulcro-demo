(ns app.utils.db
  (:require [crux.api :as crux])
  (:import [java.util UUID]))

(defn ids->idents [ids ident]
  (map
   (fn [[id]]
     {ident id})
   ids))

(defn id->ident [id ident]
  {ident id})

(defn model->ident [model]
  (if (keyword? model)
    (keyword (name model) "id")
    (keyword model "id")))

(defn submit! [node tx]
  (let [tx-map (crux/submit-tx node tx)]
    (crux/await-tx node tx-map)
    (crux/tx-committed? node tx-map)))

(defn q [node query]
  (crux/q (crux/db node) query))

(defn entity [node id]
  (crux/entity (crux/db node) id))

(defn entity-history
  ([node id]
   (entity-history node id :desc))
  ([node id sort-order]
   (crux/entity-history (crux/db node) id sort-order {:with-docs? true})))

(defn new-entity [model entity]
  (let [ident (model->ident model)
        eid (UUID/randomUUID)]
    (merge {:crux.db/id eid
            ident eid}
           (dissoc entity ident))))

(defn get-all-idents [node model]
  (let [ident (model->ident model)
        ids (q node `{:find [?e]
                      :where [[?e ~ident]]})]
    (ids->idents ids ident)))

(defn get-entity [node model id]
  (let [ident (model->ident model)
        eid (q node `{:find [?e]
                      :where [[?e ~ident ~id]]})
        eid (ffirst eid)]
    (entity node eid)))
