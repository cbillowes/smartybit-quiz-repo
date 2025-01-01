(ns smartybit-quiz.questionnaire.core
  (:require [smartybit-quiz.questionnaire.entities :as e]))


(defonce questionnaires (atom []))


(defn create
  [json-str]
  (let [questionnaire (e/str->questionnaire json-str)]
    (swap! questionnaires conj questionnaire)
    questionnaire))

