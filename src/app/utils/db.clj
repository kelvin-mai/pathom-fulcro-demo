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

(defn new-entity [ident entity]
  (let [eid (UUID/randomUUID)]
    (merge {:crux.db/id eid
            ident eid}
           (dissoc entity ident))))

(defn get-all-idents [node ident]
  (let [ids (q node `{:find [?e]
                      :where [[?e ~ident]]})]
    (ids->idents ids ident)))

(defn get-entity [node ident id]
  (let [eid (q node `{:find [?e]
                      :where [[?e ~ident ~id]]})
        eid (ffirst eid)]
    (entity node eid)))
