(ns smartybit-quiz.cli.core
  (:require [smartybit-quiz.cli.commands.core :as c]))


(defn -main [& args]
  (c/execute args))
