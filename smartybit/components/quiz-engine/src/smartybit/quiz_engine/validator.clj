(ns smartybit.quiz-engine.validator)


(defn validate-answer
  [question answer]
  {:correct? (= (:answer question) answer)
   :explanation (:explanation question)
   :answer (:answer question)})


(comment

  (validate-answer
   {:id 0 :text "What is the capital of Sweden?" :answer "Stockholm"}
   "Stockholm")


  )
