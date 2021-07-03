(ns app.api.models.inventory
  (:require [clojure.spec.alpha :as s]))

(def attrs
  [:inventory/quantity
   :inventory/product])
