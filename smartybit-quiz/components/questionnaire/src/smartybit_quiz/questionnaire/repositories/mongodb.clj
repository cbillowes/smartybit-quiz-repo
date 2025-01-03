(ns smartybit-quiz.questionnaire.repositories.mongodb
  (:require [smartybit-quiz.questionnaire.repositories.core :refer [QuestionnaireRepository]]
            [monger.core :as mg]
            [monger.collection :as mc]))


(def ^:private connection-settings (atom {}))

(defn- database-name []
  (:database @connection-settings))


(defn- collection-name []
  (:collection @connection-settings))


(defrecord MongoDbQuestionnaireRepository []
  QuestionnaireRepository

  (get-questionnaires
    [this]
    (mc/find-maps (database-name) (collection-name)))

  (get-questionnaire-by-id
    [this id]
    (mc/find-map-by-id (database-name) (collection-name) id))

  (save-questionnaire
    [this questionnaire]
    (mc/insert (database-name) (collection-name) questionnaire)))


(defn questionnaire-repository [connection-uri database-name collection-name]
  (mg/connect-via-uri connection-uri)

  (reset! connection-settings {:database database-name :collection collection-name})

  (->MongoDbQuestionnaireRepository))

