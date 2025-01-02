(ns smartybit.quiz-engine.score
  (:require [smartybit.quiz-engine.validator :as v]))


(defn score-questionnaire
  [questionnaire]
  (let [scored (map #(v/validate-answer % (:input-answer %)) questionnaire)
        score (reduce + (map :score scored))
        total (reduce + (map :score questionnaire))]
    {:score score
     :total total
     :percentage (int (/ (* 100 score) total))}))

