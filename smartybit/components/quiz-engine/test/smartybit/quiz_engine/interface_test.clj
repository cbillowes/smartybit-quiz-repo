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
      (is (= (:difficulty result) :easy))))


  (testing "No more questions for that difficulty. Go to the next difficulty and return a random question."
   (let [questionnaire [{:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                        {:id 3 :text "What is the capital of Germany?" :difficulty :easy}]
         result (sut/fetch-next-question questions :questionnaire questionnaire :difficulty :easy)]
     (is (= (:difficulty result) :medium))))


  (testing "No more questions for that difficulty nor any going up. Only lower difficulty levels are available."
    (let [questionnaire [{:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                         {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
                         {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
                         {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
                         {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
                         {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}]
          result (sut/fetch-next-question questions :questionnaire questionnaire :difficulty :easy)]
      (is (= (:difficulty result) :trivial))))


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
