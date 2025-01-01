(ns smartybit-quiz.questionnaire.interface
  (:require [smartybit-quiz.questionnaire.core :as core]))


(defn create-questionnaire
  "Creates a questionnaire using the schemas defined in `smartybit-quiz.questionnaire.entities`.
   `json-str`: the string value of the JSON contents."
  [json-str]
  (core/create json-str))


(defn all-questionnaires
  "Lists all questionnaires."
  []
  @core/questionnaires)


(comment

  (-> (slurp "/Users/clarice/Workspace/projects/smartybit-quiz-repo/smartybit-quiz/components/questionnaire/resources/seed/capitals.json")
      (create-questionnaire))

  )
