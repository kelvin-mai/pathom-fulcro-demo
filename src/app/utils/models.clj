(ns app.utils.models
  (:require [com.fulcrologic.fulcro.algorithms.tempid :refer [tempid?]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]))

(def non-blank-string?
  (s/and string?
         (complement string/blank?)))

(defn uuid-or-tempid? [id]
  (or (tempid? id)
      (uuid? id)))

(defn validate [spec params]
  (when (not (s/valid? spec params))
    (throw
     (ex-info "failed validation"
              {:transaction/success false
               :transaction/error "failed validation"
               :transaction/explain (s/explain-data spec params)}))))

(defmulti resolve-model
  (fn [model entity] model))

(defmethod resolve-model
  :default [model entity]
  entity)
