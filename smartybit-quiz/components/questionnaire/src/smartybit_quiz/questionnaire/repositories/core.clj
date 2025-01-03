(ns smartybit-quiz.questionnaire.repositories.core)


(defprotocol QuestionnaireRepository

  (get-questionnaires [this])

  (get-questionnaire-by-id [this id])

  (save-questionnaire [this questionnaire]))
