(ns smartybit-quiz.questionnaire.core
  (:require [smartybit-quiz.questionnaire.entities :as e]
            [smartybit-quiz.questionnaire.repositories.core :as repository]
            [jsonista.core :as j]))


(defn get-all-questionnaires
  [repo]
  {:pre [(satisfies? repository/QuestionnaireRepository repo)]}

  (repository/get-questionnaires repo))


(defn save-questionnaire
  [repo json-str]
  {:pre [(satisfies? repository/QuestionnaireRepository repo)]}

  (let [data (j/read-value json-str j/keyword-keys-object-mapper)
        {:keys [id]} data]

    (when id
      (throw (ex-info "This questionnaire already has an ID.")))

    (repository/save-questionnaire repo (-> data
                                            (assoc :id (java.util.UUID/randomUUID))
                                            (e/serialize-questionnaire)))))
