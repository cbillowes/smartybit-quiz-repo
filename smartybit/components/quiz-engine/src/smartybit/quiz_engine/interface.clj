(ns smartybit.quiz-engine.interface
  (:require [smartybit.quiz-engine.core :as core]))


(defn fetch-next-question
  [questions & {:keys [index direction]
                :or {index -1 direction :forward}}]
  (core/fetch-next-question questions :index index :direction direction))
