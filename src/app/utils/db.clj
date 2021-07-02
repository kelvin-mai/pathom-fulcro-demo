(ns app.utils.db
  (:require [crux.api :as crux])
  (:import [java.util UUID]))

(defn submit! [node tx]
  (let [tx-map (crux/submit-tx node tx)]
    (crux/await-tx node tx-map)
    (crux/tx-committed? node tx-map)))

(defn q [node query]
  (crux/q (crux/db node) query))

(defn entity [node id]
  (crux/entity (crux/db node) id))

(defn new-entity [ident entity]
  (let [eid (UUID/randomUUID)]
    (merge {:crux.db/id eid
            ident eid}
           entity)))

(defn entity? [entity id]
  (if entity
    entity
    (throw
     (ex-info "entity does not exists"
              {:error/message "entity does not exist"
               :entity/id id}))))

(defn ids->idents [ident ids]
  (map
   (fn [[id]]
     [ident id])
   ids))

(defn get-all-idents [node ident]
  (let [ids (q node `{:find [?e]
                      :where [[?e ~ident]]})]
    (ids->idents ident ids)))

(defn get-entity [node ident id]
  (let [eid (q node `{:find [?e]
                      :where [[?e ~ident ~id]]})
        eid (ffirst eid)]
    (crux/entity (crux/db node) eid)))