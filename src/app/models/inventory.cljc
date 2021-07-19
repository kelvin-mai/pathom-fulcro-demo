(ns app.models.inventory
  (:require app.models.product
            [clojure.spec.alpha :as s]
            [app.utils.models :as m]))

(s/def :inventory/id m/uuid-or-tempid?)
(s/def :inventory/name m/non-blank-string?)
(s/def :inventory/quantity nat-int?)
(s/def :inventory/product uuid?)

(s/def ::create
  (s/keys :req [:inventory/name
                :inventory/quantity
                :inventory/product]
          :opt [::id]))

(s/def ::update-quantity
  (s/keys :req [:inventory/id
                :inventory/quantity]))
