(ns app.models.product
  (:require [clojure.spec.alpha :as s]
            [app.utils.models :as m]))

(s/def :product/id m/uuid-or-tempid?)
(s/def :product/name m/non-blank-string?)
(s/def :product/price pos?)

(s/def ::create
  (s/keys :req [:product/name
                :product/price]
          :opt [:product/id]))
