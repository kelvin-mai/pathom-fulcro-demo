(ns app.models.inventory
  (:require app.models.product
            [clojure.spec.alpha :as s]
            [app.utils.models :as m]))

(s/def ::id m/uuid-or-tempid?)
(s/def ::name m/non-blank-string?)
(s/def ::quantity nat-int?)
(s/def ::product uuid?)

(s/def ::create
  (s/keys :req [::name
                ::quantity
                ::product]
          :opt [::id]))

(s/def ::update-quantity
  (s/keys :req [::id
                ::quantity]))
