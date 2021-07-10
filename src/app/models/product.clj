(ns app.models.product
  (:require [clojure.spec.alpha :as s]
            [app.utils.models :refer [non-blank-string?]]))

#_(s/def :product/id (complement nil?))
(s/def :product/name non-blank-string?)
(s/def :product/price pos?)

(s/def ::create
  (s/keys :req [:product/name
                :product/price]
          :opt [:product/id]))
