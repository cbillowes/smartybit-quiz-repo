(ns smartybit-quiz.questionnaire.repositories.memory
  (:require [smartybit-quiz.questionnaire.repositories.core :refer [QuestionnaireRepository]]))


(def ^:private questionnaires (atom {}))


(defrecord MemoryQuestionnaireRepository []
  QuestionnaireRepository

  (get-questionnaires
   [this]
   (vals @questionnaires))

  (get-questionnaire-by-id
   [this id]
   (get @questionnaires id))

  (save-questionnaire
   [this questionnaire]
   (swap! questionnaires assoc (:id questionnaire) questionnaire)
   questionnaire))


(defn questionnaire-repository []
  (->MemoryQuestionnaireRepository))
