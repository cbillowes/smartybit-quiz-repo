(ns smartybit.quiz-engine.interface
  (:require [smartybit.quiz-engine.core :as core]))


(defn fetch-next-question
  "Fetches the next question in the list of questions.
   The function takes a list of questions and an optional map of attributes.

   The attributes are:
   - :questionnaire (default []): The complete questionnaire for the player with the questions that have already been visited.
   - :difficulty (default :trivial): The difficulty level of the question: (:trivial, :easy, :medium, :hard, :tricky).

   Returns the next question or nil if the next question is out of bounds."
  [questions & {:keys [questionnaire difficulty]
                :or {questionnaire [] difficulty :trivial}}]
  (core/fetch-next-question questions :questionnaire questionnaire :difficulty difficulty))
