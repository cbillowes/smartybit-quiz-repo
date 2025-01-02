(ns smartybit.quiz-engine.interface-test
  (:require [clojure.test :as test :refer :all]
            [smartybit.quiz-engine.interface :as sut]))


;; This is a pool of questions that are available for the quiz.
;; Each question has a difficulty level associated with it.
;; The questionnaire that gets completed evolves based on the difficulty level of the questions.
;; The questions that have been visited will not change.
;; The future questions will be fetched with the following rules:
;; - The next question is harder than the previous question.
;; - The next question is possibly easier than the previous question if the pool is depleted of harder questions.
;; - The next question is not repeated.

(def questions [{:id 0 :text "What is the capital of Sweden?" :difficulty :trivial}
                {:id 1 :text "What is the capital of France?" :difficulty :trivial}
                {:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
                {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
                {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
                {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
                {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}])


(deftest test-fetch-next-question-by-adjusting-difficulty
  (testing "Returns only questions with the specified difficulty"
    (let [result (sut/fetch-next-question questions :difficulty :easy)]
      (is (= :easy (:difficulty result)))))


  (testing "No more questions for that difficulty. Go to the next difficulty and return a random question."
   (let [questionnaire [{:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                        {:id 3 :text "What is the capital of Germany?" :difficulty :easy}]
         result (sut/fetch-next-question questions :questionnaire questionnaire :difficulty :easy)]
     (is (= :medium (:difficulty result)))))


  (testing "No more questions for that difficulty nor any going up. Only lower difficulty levels are available."
    (let [questionnaire [{:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                         {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
                         {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
                         {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
                         {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
                         {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}]
          result (sut/fetch-next-question questions :questionnaire questionnaire :difficulty :easy)]
      (is (= :trivial (:difficulty result)))))


  (testing "No more questions. Return nil."
    (let [questionnaire [{:id 0 :text "What is the capital of Sweden?" :difficulty :trivial}
                         {:id 1 :text "What is the capital of France?" :difficulty :trivial}
                         {:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                         {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
                         {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
                         {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
                         {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
                         {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}]
          result (sut/fetch-next-question questions :questionnaire questionnaire :difficulty :easy)]
      (is (nil? result)))))


(deftest test-validate-answer
  (testing "The answer is correct"
    (let [question {:text "What is the capital of Sweden?"
                    :actual-answer "Stockholm"}
          result (sut/validate-answer question "Stockholm")]
      (is (= {:correct? true
              :explanation ""
              :score 1
              :answer "Stockholm"}
             result))))


  (testing "The answer is incorrect"
    (let [question {:text "What is the capital of Sweden?"
                    :explanation "Stockholm is the capital of Sweden."
                    :actual-answer "Stockholm"}
          result (sut/validate-answer question "Something")]
      (is (= {:correct? false
              :explanation "Stockholm is the capital of Sweden."
              :score 0
              :answer "Stockholm"}
             result)))))


(deftest test-score-questionnaire
  (testing "Returns the score of the questionnaire with everything correct."
    (let [questionnaire [{:text "What is the capital of Sweden?"
                          :actual-answer "Stockholm"
                          :input-answer "Stockholm"
                          :score 3}
                         {:text "What is the capital of France?"
                          :actual-answer "Paris"
                          :input-answer "Paris"
                          :score 10}
                         {:text "What is the capital of Mauritius?"
                          :actual-answer "Port Louis"
                          :input-answer "Port Louis"
                          :score 1}]
          result (sut/score-questionnaire questionnaire)]
      (is (=  {:score 14
               :total 14
               :percentage 100}
              result))))

  (testing "Returns the score of the questionnaire with invalid answers."
    (let [questionnaire [{:text "What is the capital of Sweden?"
                          :actual-answer "Stockholm"
                          :input-answer "Stockholm"
                          :score 3}
                         {:text "What is the capital of France?"
                          :actual-answer "Paris"
                          :input-answer "Unsure"
                          :score 10}
                         {:text "What is the capital of Mauritius?"
                          :actual-answer "Port Louis"
                          :input-answer "Port Louis"
                          :score 1}]
          result (sut/score-questionnaire questionnaire)]
      (is (=  {:score 4
               :total 14
               :percentage 28}
              result)))))
