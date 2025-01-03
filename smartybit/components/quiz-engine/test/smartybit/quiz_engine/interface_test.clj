(ns smartybit.quiz-engine.interface-test
  (:require [clojure.test :as test :refer :all]
            [smartybit.quiz-engine.interface :as sut]))


;; There is a pool of questions that are available for a quiz that is curated on the fly.
;; Each question has a difficulty level associated with it & the quiz adapts to it as follows:
;; - The level increases in difficulty when questions are answered correctly.
;; - The level decreases in difficulty when questions are answered incorrectly.
;; The next question will be fetched with the following rules:
;; - The questions are selected randomly from the pool.
;; - The first question is always a trivial question.
;; - The next question is harder than the previous question.
;; - The next question is easier than the previous question if answered incorrectly.
;; - The next question is possibly easier than the previous question if the pool is depleted of harder questions.
;; - The next question is never repeated.

;; A place to store a list of questions we can use to curate a quiz.
(def ^:private question-pool [{:id 0 :text "What is the capital of Sweden?" :difficulty :trivial :answer "Stockholm"}
                              {:id 1 :text "What is the capital of France?" :difficulty :trivial :answer "Paris"}
                              {:id 2 :text "What is the capital of Mauritius?" :difficulty :easy :answer "Port Louis"}
                              {:id 3 :text "What is the capital of Germany?" :difficulty :easy :answer "Berlin"}
                              {:id 4 :text "What is the capital of Italy?" :difficulty :medium :answer "Rome"}
                              {:id 5 :text "What is the capital of Japan?" :difficulty :hard :answer "Tokyo"}
                              {:id 6 :text "What is the capital of Australia?" :difficulty :tricky :answer "Canberra"}
                              {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky :answer "Brasília"}])


(deftest test-quiz-curation
  (testing "The first question is always a trivial question.
            No questionnaire or difficulty is provided to the API."
    (let [result (sut/fetch-next-question question-pool)]
      (is (= :trivial (:difficulty result)))))


  (testing "The next question is harder than the previous question:
            trivial -> easy."
    (let [quiz [{:id 0 :answer "Stockholm"}]
          index 0
          result (sut/fetch-next-question question-pool :quiz quiz :index index)]
      (is (= :easy (:difficulty result)))))


  (testing "The next question is harder than the previous question:
              hard -> tricky"
    (let [quiz [{:id 5 :answer "Tokyo"}]
          index 0
          result (sut/fetch-next-question question-pool :quiz quiz :index index)]
      (is (= :easy (:difficulty result)))))
  
  
  )





(deftest test-fetch-next-question-by-adjusting-difficulty2
  (testing "Returns only questions with the specified difficulty"
    (let [result (sut/fetch-next-question question-pool :difficulty :easy)]
      (is (= :easy (:difficulty result)))))


  (testing "No more questions for that difficulty. Go to the next difficulty and return a random question."
   (let [questionnaire [{:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                        {:id 3 :text "What is the capital of Germany?" :difficulty :easy}]
         result (sut/fetch-next-question question-pool :questionnaire questionnaire :difficulty :easy)]
     (is (= :medium (:difficulty result)))))


  (testing "No more questions for that difficulty nor any going up. Only lower difficulty levels are available."
    (let [questionnaire [{:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                         {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
                         {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
                         {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
                         {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
                         {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}]
          result (sut/fetch-next-question question-pool :questionnaire questionnaire :difficulty :easy)]
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
          result (sut/fetch-next-question question-pool :questionnaire questionnaire :difficulty :easy)]
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
