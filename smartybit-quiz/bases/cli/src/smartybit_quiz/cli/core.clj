(ns smartybit-quiz.cli.core
  (:require [smartybit-quiz.cli.commands :as c]))

(defn -main [& args]
  (c/execute args))
