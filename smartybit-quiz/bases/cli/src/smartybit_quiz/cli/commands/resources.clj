(ns smartybit-quiz.cli.commands.resources
  (:require [clojure.string :as str]))


(defmulti cli-resource
  "A registered resource to be used in the CLI. Expects `resource`, `command` and `args`."
  (fn [resource command args] resource))


(defmethod cli-resource :default [resource command _]
  (println (format "Invalid command '%s' and resource '%s' combination. Use 'help' to see available commands." command resource)))


(defn registered-resources
  []
  (->> (methods cli-resource)
       (map first)
       (str/join ", ")))
