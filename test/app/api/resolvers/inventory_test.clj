(ns app.api.resolvers.inventory-test
  (:require [clojure.test :refer [use-fixtures
                                  deftest
                                  testing
                                  is]]
            [app.api.test-utils :refer [use-system test-system]]))

(use-fixtures :once (use-system))

(defn mock-product [wrapped-parser]
  (let [query `[{(mutation/create-product {:product/name "test"
                                           :product/price 22.5})
                 [:product/id
                  :product/name
                  :product/price]}]
        parsed (wrapped-parser query)
        result (get parsed `mutation/create-product)]
    result))

(deftest inventory-test
  (let [{parser :pathom/parser
         db :crux/db} @test-system
        parse #(parser {:db db} %)
        product (mock-product parse)
        product-id (:product/id product)
        query `[{(mutation/create-inventory {:inventory/name "test inventory"
                                             :inventory/quantity 69
                                             :inventory/product {:product/id ~product-id}})
                 [:transaction/success
                  :inventory/id
                  :inventory/name
                  :inventory/quantity
                  :inventory/history
                  {:inventory/product [:product/id
                                       :product/name
                                       :product/price]}]}]
        parsed (parse query)
        result (get parsed `mutation/create-inventory)
        test-id (:inventory/id result)]
    (testing "can create inventory"
      (is (= true
             (:transaction/success result)))
      (is (= "test inventory"
             (:inventory/name result)))
      (is (= 69
             (:inventory/quantity result)))
      (is (= product
             (:inventory/product result)))
      (is (= 1
             (count (:inventory/history result)))))

    (testing "can query created inventory"
      (let [query [{[:inventory/id test-id]
                    [:inventory/id
                     :inventory/name
                     :inventory/quantity
                     :inventory/history
                     {:inventory/product [:product/id
                                          :product/name
                                          :product/price]}]}]
            parsed (parse query)
            result (get parsed [:inventory/id test-id])]
        (is (= test-id
               (:inventory/id result)))
        (is (= "test inventory"
               (:inventory/name result)))
        (is (= 69
               (:inventory/quantity result)))
        (is (= product
               (:inventory/product result)))
        (is (= 1
               (count (:inventory/history result))))))

    (testing "can update inventory quantity"
      (let [transaction-query `[{(mutation/update-inventory-quantity {:inventory/id ~test-id
                                                                      :inventory/quantity 100})
                                 [:transaction/success
                                  :inventory/id
                                  :inventory/name
                                  :inventory/quantity
                                  :inventory/history
                                  {:inventory/product [:product/id
                                                       :product/name
                                                       :product/price]}]}]
            parsed (parse transaction-query)
            result (get parsed `mutation/update-inventory-quantity)]
        (is (= true
               (:transaction/success result)))
        (is (= test-id
               (:inventory/id result)))
        (is (= "test inventory"
               (:inventory/name result)))
        (is (= 100
               (:inventory/quantity result)))
        (is (= product
               (:inventory/product result)))
        (is (= 2
               (count (:inventory/history result))))))))
