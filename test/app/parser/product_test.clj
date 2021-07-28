(ns app.parser.product-test
  (:require [clojure.test :refer [use-fixtures
                                  deftest
                                  testing
                                  is]]
            [app.models.product.mutations :as product.mutation]
            [app.models.product :as product]
            [app.test-utils :refer [use-system test-system]]))

(use-fixtures :once (use-system))

(deftest product-test
  (let [{parser :pathom/parser} @test-system
        parse #(parser {} %)
        query `[{(product.mutation/create! {::product/name "test"
                                            ::product/price 22.5})
                 [:transaction/success
                  ::product/id
                  ::product/name
                  ::product/price]}]
        parsed (parse query)
        result (get parsed `product.mutation/create!)
        test-id (::product/id result)]
    (testing "can create product"
      (is (= true
             (:transaction/success result)))
      (is (= "test"
             (::product/name result)))
      (is (= 22.5
             (::product/price result))))

    (testing "query created product"
      (let [query [{[::product/id test-id]
                    [::product/id
                     ::product/name
                     ::product/price]}]
            parsed (parse query)
            result (get parsed [::product/id test-id])]
        (is (= test-id
               (::product/id result)))
        (is (= "test"
               (::product/name result)))
        (is (= 22.5
               (::product/price result)))))))
