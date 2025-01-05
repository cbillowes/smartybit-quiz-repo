(ns smartybit.quiz-engine.core-test
  (:require [clojure.test :as test :refer :all]
            [smartybit.quiz-engine.core :as sut]))


(def ^:private question-pool
  [{:id "1" :text "What is the capital of Sweden?" :difficulty :trivial :score 1 :answer "Stockholm"}
   {:id "2" :text "What is the capital of France?" :difficulty :trivial :score 1 :answer "Paris"}
   {:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 1 :answer "Port Louis"}
   {:id "4" :text "What is the capital of Germany?" :difficulty :easy :score 1 :answer "Berlin"}
   {:id "5" :text "What is the capital of Italy?" :difficulty :medium :score 1 :answer "Rome"}
   {:id "6" :text "What is the capital of Japan?" :difficulty :hard :score 1 :answer "Tokyo"}
   {:id "7" :text "What is the capital of Australia?" :difficulty :tricky :score 1 :answer "Canberra"}
   {:id "8" :text "What is the capital of Brazil?" :difficulty :tricky :score 1 :answer "Brasília"}])


(deftest test-answer-question
  (testing "Correct answer should add score points based on the question, mark the question as correct and continue the streak"
    (let [quiz [{:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 10 :answer "Port Louis"}]
          state {:quiz quiz :streak 3}
          user-input {:id "3" :answer "Port Louis"}
          actual (sut/answer-question state user-input)]
      (is (= {:score 10
              :streak 4
              :correct? true
              :difficulty :easy
              :quiz [{:id "3"
                      :text "What is the capital of Mauritius?"
                      :difficulty :easy
                      :score 10
                      :correct? true
                      :answer {:actual "Port Louis" :got "Port Louis"}}]} actual))))


  (testing "Incorrect answer should not add score points, mark the question as incorrect and reset the streak"
    (let [quiz [{:id "3" :text "What is the capital of Thailand?" :difficulty :easy :score 1 :answer "Bangkok"}]
          state {:quiz quiz}
          user-input {:id "3" :answer "Port Louis"}
          actual (sut/answer-question state user-input)]
      (is (= {:score 0
              :streak 0
              :correct? false
              :difficulty :easy
              :quiz [{:id "3"
                      :text "What is the capital of Thailand?"
                      :difficulty :easy
                      :score 1
                      :correct? false
                      :answer {:actual "Bangkok" :got "Port Louis"}}]} actual))))

  (testing "Ensure the rest of the quiz is not affected"
    (let [quiz [{:id "1" :text "What is the capital of Sweden?" :difficulty :trivial :score 1 :answer "Stockholm"}
                {:id "2" :text "What is the capital of France?" :difficulty :trivial :score 1 :answer "Paris"}
                {:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 1 :answer "Port Louis"}
                {:id "4" :text "What is the capital of Germany?" :difficulty :easy :score 1 :answer "Berlin"}]
          state {:quiz quiz}
          user-input {:id "3" :answer "Port Louis"}
          actual (sut/answer-question state user-input)]
      (is (= {:score 1
              :correct? true
              :streak 1
              :difficulty :easy
              :quiz [{:id "1" :text "What is the capital of Sweden?" :difficulty :trivial :score 1 :answer "Stockholm"}
                     {:id "2" :text "What is the capital of France?" :difficulty :trivial :score 1 :answer "Paris"}
                     {:id "3"
                      :text "What is the capital of Mauritius?"
                      :difficulty :easy
                      :score 1
                      :correct? true
                      :answer {:actual "Port Louis" :got "Port Louis"}}
                     {:id "4" :text "What is the capital of Germany?" :difficulty :easy :score 1 :answer "Berlin"}]} actual))))


  (testing "Does nothing if the question is not found in the quiz"
    (let [quiz []
          state {:quiz quiz}
          user-input {:id "3" :answer "Something obscure"}
          actual (sut/answer-question state user-input)]
      (is (= {:correct? false :streak 0 :score 0 :quiz [] :difficulty nil} actual)))))


(deftest test-adjust-difficulty
  (testing "Correct answer should increase the difficulty"
    (let [actual (sut/adjust-difficulty {:correct? true :difficulty :easy})]
      (is (= :medium (:difficulty actual)))))

  (testing "Incorrect answer should decrease the difficulty"
    (let [actual (sut/adjust-difficulty {:correct? false :difficulty :easy})]
      (is (= :trivial (:difficulty actual)))))

  (testing "Adjusting difficulty downwards from :any should always return the lowest difficulty"
    (let [actual (sut/adjust-difficulty {:correct? false :difficulty :any})]
      (is (= :trivial (:difficulty actual)))))

  (testing "Adjusting difficulty upwards from the highest difficulty should remain in the highest difficulty"
    (let [actual (sut/adjust-difficulty {:correct? true :difficulty :tricky})]
      (is (= :tricky (:difficulty actual)))))

  (testing "Adjusting difficulty upwards from an unspecified difficulty should always return the lowest difficulty"
    (let [actual (sut/adjust-difficulty {:correct? false})]
      (is (= :trivial (:difficulty actual)))))

  (testing "If the pool is depleted of a certain difficulty, the difficulty should be further adjusted"
    (let [actual (sut/adjust-difficulty {:correct? true :difficulty :easy :pool [{:difficulty :hard}]})]
      (is (= :hard (:difficulty actual)))))

  (testing "If the pool is depleted of a certain difficulty, the difficulty should be circularly adjusted"
    (let [actual (sut/adjust-difficulty {:correct? true :difficulty :hard :pool [{:difficulty :easy}]})]
      (is (= :easy (:difficulty actual))))))



(deftest test-pick-next-question
  (testing "Should remove the picked question from the pool"
    (let [state {:pool question-pool :difficulty :easy}
          {:keys [pool next-question]} (sut/pick-next-question state)]
      (is (empty? (filter #(= (:id %) (:id next-question)) pool)))))

  (testing "Should add the picked question from the pool to the quiz"
    (let [state {:pool question-pool :difficulty :easy}
          {:keys [quiz next-question]} (sut/pick-next-question state)]
      (is (false? (empty? (filter #(= (:id %) (:id next-question)) quiz))))))

  (testing "Should return a random question from the pool of that difficulty"
    (let [state {:pool question-pool :difficulty :easy}
          {:keys [next-question]} (sut/pick-next-question state)]
      (is (= :easy (:difficulty next-question))))))



(deftest test-next-question
  (testing "Should return next question"
    (let [state {:pool question-pool :difficulty :easy}
          user-input {:id "3" :answer "Port Louis"}
          actual (sut/fetch-next-question state user-input)]
      (is (contains? actual :next-question)))))
