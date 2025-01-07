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
  (testing "Does nothing if the question is not found in the quiz"
    (let [quiz []
          state {:quiz quiz}
          user-input {:id "3" :answer "Something obscure"}
          actual (sut/answer-question state user-input)]
      (is (= {:advance? false :streak 0 :score 0 :quiz [] :difficulty nil} actual))))

  (testing "Correct answer should add score points for that question"
    (let [quiz [{:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 10 :answer "Port Louis"}]
          state {:quiz quiz :streak 3 :score 5}
          user-input {:id "3" :answer "Port Louis"}
          actual (sut/answer-question state user-input)]
      (is (= 15 (:score actual)))))

  (testing "Correct answer should increase the streak"
    (let [quiz [{:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 1 :answer "Port Louis"}]
          state {:quiz quiz :streak 3}
          user-input {:id "3" :answer "Port Louis"}
          actual (sut/answer-question state user-input)]
      (is (= 4 (:streak actual)))))

  (testing "Incorrect answer should reset the streak"
    (let [quiz [{:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 1 :answer "Port Louis"}]
          state {:quiz quiz :streak 3}
          user-input {:id "3" :answer "Wrong"}
          actual (sut/answer-question state user-input)]
      (is (= 0 (:streak actual)))))

  (testing "The difficulty of the quiz is the difficulty of the current question"
    (let [quiz [{:id "3" :text "What is the capital of Mauritius?" :difficulty :medium :score 1 :answer "Port Louis"}]
          state {:quiz quiz}
          user-input {:id "3" :answer "Port Louis"}
          actual (sut/answer-question state user-input)]
      (is (= :medium (:difficulty actual)))))

  (testing "Correct answer should advance the difficulty"
    (let [quiz [{:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 1 :answer "Port Louis"}]
          state {:quiz quiz}
          user-input {:id "3" :answer "Port Louis"}
          actual (sut/answer-question state user-input)]
      (is (true? (:advance? actual)))))

  (testing "Incorrect answer should not advance the difficulty"
    (let [quiz [{:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 1 :answer "Port Louis"}]
          state {:quiz quiz}
          user-input {:id "3" :answer "Wrong"}
          actual (sut/answer-question state user-input)]
      (is (false? (:advance? actual)))))

  (testing "Should add the timestamp the question was answered"
    (with-redefs [sut/current-time-in-millis (constantly 1736137133999)]
      (let [quiz [{:id "3" :text "What is the capital of Mauritius?" :difficulty :easy :score 1 :answer "Port Louis"}]
            state {:quiz quiz}
            user-input {:id "3" :answer "Port Louis"}
            actual (sut/answer-question state user-input)]
        (is (= 1736137133999 (:answered-at (first (:quiz actual)))))))))


(deftest test-get-next-difficulty
  (testing "Should return the next difficulty level when advancing the difficulty"
    (is (= :trivial (sut/get-next-difficulty nil true)))
    (is (= :easy (sut/get-next-difficulty :trivial true)))
    (is (= :medium (sut/get-next-difficulty :easy true)))
    (is (= :hard (sut/get-next-difficulty :medium true)))
    (is (= :tricky (sut/get-next-difficulty :hard true)))
    (is (= :trivial (sut/get-next-difficulty :tricky true))))


  (testing "Should return the next difficulty level when not advancing the difficulty"
    (is (= :trivial (sut/get-next-difficulty nil false)))
    (is (= :trivial (sut/get-next-difficulty :trivial false)))
    (is (= :trivial (sut/get-next-difficulty :easy false)))
    (is (= :easy (sut/get-next-difficulty :medium false)))
    (is (= :medium (sut/get-next-difficulty :hard false)))
    (is (= :hard (sut/get-next-difficulty :tricky false)))))


(deftest test-adjust-difficulty
  (testing "Correct answer should increase the difficulty"
    (let [actual (sut/adjust-difficulty {:advance? true :difficulty :easy :pool [{:difficulty :medium}]})]
      (is (= :medium (:difficulty actual)))))

  (testing "Incorrect answer should decrease the difficulty"
    (let [actual (sut/adjust-difficulty {:advance? false :difficulty :easy :pool [{:difficulty :trivial}]})]
      (is (= :trivial (:difficulty actual)))))

  (testing "Adjusting difficulty downwards from :any should always return the lowest difficulty"
    (let [actual (sut/adjust-difficulty {:advance? false :difficulty :any :pool [{:difficulty :trivial}]})]
      (is (= :trivial (:difficulty actual)))))

  (testing "Adjusting difficulty upwards from the highest difficulty should remain in the highest difficulty"
    (let [actual (sut/adjust-difficulty {:advance? true :difficulty :tricky :pool [{:difficulty :tricky}]})]
      (is (= :tricky (:difficulty actual)))))

  (testing "Adjusting difficulty upwards from an unspecified difficulty should always return the lowest difficulty"
    (let [actual (sut/adjust-difficulty {:advance? false :pool [{:difficulty :trivial}]})]
      (is (= :trivial (:difficulty actual)))))

  (testing "If the pool is depleted of a certain difficulty, the difficulty should be further adjusted"
    (let [actual (sut/adjust-difficulty {:advance? true :difficulty :easy :pool [{:difficulty :hard}]})]
      (is (= :hard (:difficulty actual)))))

  (testing "If the pool is depleted of a certain difficulty, the difficulty should be circularly adjusted"
    (let [actual (sut/adjust-difficulty {:advance? true :difficulty :hard :pool [{:difficulty :medium}]})]
      (is (= :medium (:difficulty actual))))))



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
  (testing "Should return a trivial question as the first question"
    (let [state {:pool question-pool :difficulty :easy}
          user-input {:id "3" :answer "Port Louis"}]
      (is (= :trivial (:difficulty (:next-question (sut/fetch-next-question state user-input)))))
      (is (= :trivial (:difficulty (:next-question (sut/fetch-next-question state user-input)))))
      (is (= :trivial (:difficulty (:next-question (sut/fetch-next-question state user-input)))))
      (is (= :trivial (:difficulty (:next-question (sut/fetch-next-question state user-input)))))
      (is (= :trivial (:difficulty (:next-question (sut/fetch-next-question state user-input)))))))

  (testing "Should contain the timestamp of the question of when it was added to the quiz"
    (with-redefs [sut/current-time-in-millis (constantly 1736137133999)]
      (let [state {:pool question-pool :difficulty :easy}
            user-input {:id "3" :answer "Port Louis"}
            actual (sut/fetch-next-question state user-input)]
        (is (= 1736137133999 (get-in actual [:next-question :picked-at]))))))

  #_(testing "Should advance the game when questions are answered correctly forming a streak"
    (let [state-1 (sut/fetch-next-question {:pool question-pool} {})
          state-2 (sut/fetch-next-question state-1 (:next-question state-1))
          state-3 (sut/fetch-next-question state-2 (:next-question state-2))
          state-4 (sut/fetch-next-question state-3 (:next-question state-3))
          state-5 (sut/fetch-next-question state-4 (:next-question state-4))
          state-6 (sut/fetch-next-question state-5 (:next-question state-5))]
      (clojure.pprint/pprint state-6)
      (is (= :trivial (get-in state-1 [:next-question :difficulty])))
      (is (= :easy (get-in state-2 [:next-question :difficulty])))
      (is (= :medium (get-in state-3 [:next-question :difficulty])))
      (is (= 2 (:score state-3))))))
