(ns app.models.inventory
  (:require app.models.product
            [clojure.spec.alpha :as s]
            [app.utils.models :refer [non-blank-string?]]))

(def attrs
  [:inventory/name
   :inventory/quantity
   :inventory/product])

(s/def :inventory/id uuid?)
(s/def :inventory/name non-blank-string?)
(s/def :inventory/quantity nat-int?)
(s/def :inventory/product
  (s/keys :req [:product/id]))

(s/def ::create
  (s/keys :req [:inventory/name
                :inventory/quantity
                :inventory/product]))

(s/def ::update-quantity
  (s/keys :req [:inventory/id
                :inventory/quantity]))
