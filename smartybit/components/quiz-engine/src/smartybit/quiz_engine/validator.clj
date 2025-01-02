(ns smartybit.quiz-engine.validator)


(defn validate-answer
  [question answer]
  (let [correct? (= (:actual-answer question) answer)]
    {:correct? correct?
     :explanation (:explanation question "")
     :score (if correct? (:score question 1) 0)
     :answer (:actual-answer question)}))


(comment

  (validate-answer
   {:id 0 :text "What is the capital of Sweden?" :actual-answer "Stockholm"}
   "Stockholm")


  )
