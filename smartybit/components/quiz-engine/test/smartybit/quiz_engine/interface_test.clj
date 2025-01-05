(ns smartybit.quiz-engine.interface-test
  (:require [clojure.test :as test :refer :all]
            [smartybit.quiz-engine.interface :as sut]))


(deftest test-fetch-next-question-to-start-the-quiz
  (let [state {:pool [{:id "1" :text "What is the first item in the period table?" :difficulty :trivial :score 1 :answer "Hydrogen"}
                      {:id "2" :text "What is the second item in the period table?" :difficulty :easy :score 1 :answer "Helium"}
                      {:id "3" :text "What is the third item in the period table?" :difficulty :medium :score 1 :answer "Lithium"}
                      {:id "4" :text "What is the fourth item in the period table?" :difficulty :hard :score 1 :answer "Beryllium"}
                      {:id "5" :text "What is the fifth item in the period table?" :difficulty :tricky :score 1 :answer "Boron"}]
               :quiz []
               :score 0
               :streak 0}
        user-input {} ;; Nothing happened yet
        {:keys [pool quiz next-question score streak]} (sut/fetch-next-question state user-input)]
    (testing "A trivial question should be returned"
      (is (= :trivial (:difficulty next-question))))

    (testing "The question should be in the quiz"
      (is (= [{:id "1" :text "What is the first item in the period table?" :difficulty :trivial :score 1 :answer "Hydrogen"}]
             quiz)))

    (testing "The question should be removed from the pool"
      (is (= [;; removed from pool => {:id "1" :text "What is the first item in the period table?" :difficulty :trivial :score 1 :answer "Hydrogen"}
              {:id "2" :text "What is the second item in the period table?" :difficulty :easy :score 1 :answer "Helium"}
              {:id "3" :text "What is the third item in the period table?" :difficulty :medium :score 1 :answer "Lithium"}
              {:id "4" :text "What is the fourth item in the period table?" :difficulty :hard :score 1 :answer "Beryllium"}
              {:id "5" :text "What is the fifth item in the period table?" :difficulty :tricky :score 1 :answer "Boron"}]
             pool)))

    (testing "Defaults should remain in tact"
      (is (= 0 score) "Score")
      (is (= 0 streak) "Streak"))))
