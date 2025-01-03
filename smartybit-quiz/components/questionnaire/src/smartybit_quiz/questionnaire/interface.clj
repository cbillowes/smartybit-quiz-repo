(ns smartybit-quiz.questionnaire.interface
  (:require [smartybit-quiz.questionnaire.core :as core]
            [smartybit-quiz.questionnaire.repositories.memory :as memory]))


(def ^:private repository (atom nil))


(defn initialize-repository!
  "Initializes the repository to be used by the interface."
  [repo]
  (reset! repository repo))


(defn get-questionnaires
  "Lists all questionnaires."
  []
  (core/get-all-questionnaires @repository))


(defn save-questionnaire
  ;; TODO
  "Persists a questionnaire, described by JSON, to the initialized repository using the schemas defined in `smartybit-quiz.questionnaire.entities`.
   If the questionnaire already exists (has an ID associated with it), then an exception is thrown.
   If the questionnaire does not meet the schema's expectations then an exception is thrown.
   Returns the questionnaire."
  [json-str]
  (core/save-questionnaire @repository json-str))




(comment


  (require '[smartybit-quiz.questionnaire.repositories.memory :as memory]
           '[smartybit-quiz.questionnaire.repositories.mongodb :as mongodb])

  (initialize-repository! (mongodb/questionnaire-repository "mongodb://admin:password@localhost:27017" "smartybit-quiz" "questionnaires"))

  (-> (slurp "/Users/clarice/Workspace/projects/smartybit-quiz-repo/smartybit-quiz/components/questionnaire/resources/seed/capitals.json")
      (save-questionnaire))


  (get-questionnaires)
  )
