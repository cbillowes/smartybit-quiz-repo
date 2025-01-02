(ns smartybit.quiz-engine.interface-test
  (:require [clojure.test :as test :refer :all]
            [smartybit.quiz-engine.interface :as sut]))


(deftest test-fetch-next-question
  (testing "Can fetch the next question in the list (moving forward)"
    (let [questions [{:text "What is the capital of France?"}
                     {:text "What is the capital of Germany?"} ;; <- (index 1) we are here
                     {:text "What is the capital of Italy?"}]
          result (sut/fetch-next-question questions :index 1)]
      (is (= (:text result) "What is the capital of Italy?")))))
