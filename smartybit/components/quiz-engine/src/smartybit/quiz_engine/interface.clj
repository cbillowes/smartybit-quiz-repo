(ns smartybit.quiz-engine.interface
  (:require [smartybit.quiz-engine.core :as core]))


(defn fetch-next-question
  [questions & {:keys [index]}]
  (core/fetch-next-question questions :index index))
