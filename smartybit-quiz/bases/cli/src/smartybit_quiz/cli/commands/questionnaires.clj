(ns smartybit-quiz.cli.commands.questionnaires
  (:require [smartybit-quiz.cli.commands.resources :as r]
            [smartybit-quiz.questionnaire.interface :as questionnaire]
            [clojure.pprint :as pp]))


(defn- try-create-questionnaire [json-str]
  (try
    (questionnaire/create-questionnaire json-str)
    (catch Exception e
      (println (format "Error creating questionnaire: %s" (.getMessage e))))))


(defn- list-questionnaires []
  (let [all (questionnaire/all-questionnaires)]
    (if (empty? all)
      (println "No questionnaires found.")
      (println (with-out-str (pp/print-table (map #(select-keys % [:id :title :status :private?]) all)))))))


(defn- create-questionnaire [[source type uri]]
  (cond
    (= source "-f")
    (cond
      (= type "json")
      (let [json-str (slurp uri)
            questionnaire (try-create-questionnaire json-str)]
        (println (format "Created questionnaire '%s' with id '%s'." (:title questionnaire) (:id questionnaire)))
        (r/cli-resource "questionnaire" "list" ""))

      :else (println "Unknown type: " type))

    :else
    (println "Missing file source flag -f with corresponding type and filename values.\nSupported types: 'json'.\nUsage: create questionnaire -f <type> <filename>")))


(defmethod r/cli-resource "questionnaire" [_ command args]
  (cond
    (= command "list") (list-questionnaires)
    (= command "create") (create-questionnaire args)
    (= command "get") (println "Get questionnaire")
    (= command "delete") (println "Delete questionnaire")
    (= command "update") (println "Update questionnaire")
    :else (println "Unknown command")))
