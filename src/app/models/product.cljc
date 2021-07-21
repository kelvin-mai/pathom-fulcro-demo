(ns app.models.product
  (:require [clojure.spec.alpha :as s]
            [app.utils.models :as m]))

(s/def ::id m/uuid-or-tempid?)
(s/def ::name m/non-blank-string?)
(s/def ::price pos?)

(s/def ::create
  (s/keys :req [::name
                ::price]
          :opt [::id]))

(s/def ::update
  (s/keys :req [::id
                ::name
                ::price]))

(s/def ::delete
  (s/keys :req [::id
                ::name
                ::price]))
