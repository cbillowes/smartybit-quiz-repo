(ns smartybit.quiz-engine.interface
  (:require [smartybit.quiz-engine.core :as core]))


(defn fetch-next-question
  "There is a pool of questions that are available for a quiz that is curated on the fly.
   Each question has a difficulty level associated with it & the quiz adapts to it.
   The next question will be fetched with the following rules:
   - The questions are selected randomly from the pool of available questions.
   - The question is removed from the pool when it is picked to ensure it is never repeated.
   - The first question is always a trivial question.
   - The next question is harder than the previous question.
   - The next question is easier than the previous question if answered incorrectly.
   - The next question is possibly easier than the previous question if the pool is depleted of harder questions.

   Inputs:
   - state: A map of the state of the current questionnaire with the following keys:
      - :pool: A list of questions available for the quiz.
      - :quiz: A list of questions that have been answered.
      - :score: The current score of the quiz.
      - :streak: The current streak of correct answers.
   - user-input: A map of the user's input with the following keys:
      - :id: The id of the question that the user is answering.
      - :answer: The user's answer to the question.

   Returns a map of the state of the current questionnaire with the following keys:
   - pool
   - quiz
   - score
   - streak
   - difficulty
   - next-question"
  [{:keys [pool quiz score streak] :as state} {:keys [id answer] :as user-input}]
  (core/fetch-next-question state user-input))

