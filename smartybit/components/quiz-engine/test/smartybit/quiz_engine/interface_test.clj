(ns smartybit.quiz-engine.interface-test
  (:require [clojure.test :as test :refer :all]
            [smartybit.quiz-engine.interface :as sut]))


(deftest test-fetch-next-question
  (testing "Returns the first question when attributes are not provided"
    (let [questions [{:text "What is the capital of France?"}
                     {:text "What is the capital of Mauritius?"}
                     {:text "What is the capital of Germany?"}
                     {:text "What is the capital of Italy?"}]
          result (sut/fetch-next-question questions)]
      (is (= (:text result) "What is the capital of France?"))))


  (testing "Can fetch the next question in the list (moving forward)"
    (let [questions [{:text "What is the capital of France?"}
                     {:text "What is the capital of Mauritius?"}
                     {:text "What is the capital of Germany?"} ;; <- (index 2) we are here
                     {:text "What is the capital of Italy?"}]
          result (sut/fetch-next-question questions :index 2 :direction :forward)]
      (is (= (:text result) "What is the capital of Italy?"))))


  (testing "Can fetch the next question in the list (moving backward)"
    (let [questions [{:text "What is the capital of France?"}
                     {:text "What is the capital of Mauritius?"}
                     {:text "What is the capital of Germany?"} ;; <- (index 2) we are here
                     {:text "What is the capital of Italy?"}]
          result (sut/fetch-next-question questions :index 2 :direction :backward)]
      (is (= (:text result) "What is the capital of Mauritius?"))))


  (testing "Returns nil when the next question is out of bounds (moving forward)"
    (let [questions [{:text "What is the capital of France?"}
                     {:text "What is the capital of Mauritius?"}
                     {:text "What is the capital of Germany?"}
                     {:text "What is the capital of Italy?"} ;; <- (index 3) we are here
                     ]
          result (sut/fetch-next-question questions :index 3 :direction :forward)]
      (is (nil? result))))


  (testing "Returns nil when the next question is out of bounds (moving backward)"
    (let [questions [{:text "What is the capital of France?"} ;; <- (index 0) we are here
                     {:text "What is the capital of Mauritius?"}
                     {:text "What is the capital of Germany?"}
                     {:text "What is the capital of Italy?"}
                     ]
          result (sut/fetch-next-question questions :index 0 :direction :backward)]
      (is (nil? result)))))
