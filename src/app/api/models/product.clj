(ns app.api.models.product
  (:require [clojure.spec.alpha :as s]))

(def attrs
  [:product/name
   :product/price])
