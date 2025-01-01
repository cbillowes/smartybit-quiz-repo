(ns smartybit-quiz.cli.commands.core
  (:require [smartybit-quiz.cli.commands.resources :as r]
            ;; required because we need the registered resources
            [smartybit-quiz.cli.commands.questionnaires]))


(defn- banner
  "Renders a banner for the CLI generated from https://www.asciiart.eu/ascii-draw-studio/app."
  []
  "
... -- .- .-. - -.-- -... .. -
Welcome to the SmartyBit Quiz
   ")


(defn- help [args]
  (println (banner))
  (let [command (first args)]
    (if command
      (case command
        "list" (println "Lists all available data for a given resource.\nUsage: list <resource>.\nExample: list quiz")
        "get" (println "Gets applicable information for a given resource based on its identity.\nUsage: get <resource> <resource-id>\nExample: get quiz 1")
        "create" (println "Creates a resource from data within a given file, if allowed to do so.\nUsage: create <resource> -f <filename>\nExample: create quiz -f quiz.json")
        "edit" (println "Change the data for a given resource, if allowed to do so.\nUsage: edit <resource> <resource-id>\nExample: edit quiz 1")
        "delete" (println "Destructively deletes a resource, if allowed to do so. No confirmation, no rollback. Just delete.\nUsage: delete <resource> <resource-id>\nExample: delete quiz 1")
        (println (str "Unknown command: " command ". Use 'help' to see available commands.")))
      (println
       (format "Usage: command <resource> <resource-id>*
Commands: help, list, get, create, edit, delete.
Resources: %s.
Use 'help <command>' for detailed information about a command." (r/registered-resources))))))


(defn execute [args]
  (println (banner))
  (let [[command resource & args] args]
    (cond
      (= "help" command)
      (help (rest args))

      (empty? resource)
      (println "A resource is required. Use 'help' to see available resources.")

      :else
      (r/cli-resource resource command args))))
