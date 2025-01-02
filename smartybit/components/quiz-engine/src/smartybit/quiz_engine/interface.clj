(ns smartybit.quiz-engine.interface
  (:require [smartybit.quiz-engine.next-question :as nq]
            [smartybit.quiz-engine.validator :as v]
            [smartybit.quiz-engine.score :as s]))


(defn fetch-next-question
  "Fetches the next question in the list of questions.
   The function takes a list of questions and an optional map of attributes.

   The attributes are:
   - :questionnaire (default []): The complete questionnaire for the player with the questions that have already been visited.
   - :difficulty (default :trivial): The difficulty level of the question: (:trivial, :easy, :medium, :hard, :tricky).

   Returns the next question or nil if the next question is out of bounds."
  [questions & {:keys [questionnaire difficulty]
                :or {questionnaire [] difficulty :trivial}}]
  (nq/fetch-next-question questions :questionnaire questionnaire :difficulty difficulty))


(defn validate-answer
  "Validates the answer to a question.

   Returns a map with the result of the validation which includes:
   - correct? true if the answer is correct, false otherwise.
   - explanation: a string with the explanation of the answer.
   - answer: the expected correct answer.
   - score: the score of the question."
  [question answer]
  (v/validate-answer question answer))


(defn score-questionnaire
  "Scores the answers in a given questionnaire.
   - Each question should contain at least the following keys:
     - :actual-answer: the correct answer.
     - :input-answer: the answer given by the player.
     - :score: the score of the question.

   Returns the score at that point of the questionnaire."
  [questionnaire]
  (s/score-questionnaire questionnaire))
