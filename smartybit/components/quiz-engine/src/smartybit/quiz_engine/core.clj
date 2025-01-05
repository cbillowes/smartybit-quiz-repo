(ns smartybit.quiz-engine.core)

;; The :any difficulty level is a circular marker to move the difficulty levels
;; back to the beginning when the pool is depleted of questions of a certain difficulty.
(def ^:private difficulty-levels [:any :trivial :easy :medium :hard :tricky])


(defn answer-question [{:keys [quiz score streak] :as state
                        :or {quiz [] score 0 streak 0}}
                       {:keys [id answer]}]
  (let [question (->> quiz
                      (filter #(= (:id %) id))
                      (first))
        correct?  (= (:answer question) answer)
        question (assoc question
                        :correct? correct?
                        :answer {:got answer :actual (:answer question)})]
    (merge state {:quiz (map #(if (= (:id %) id) question %) quiz)
                  :score (if (:correct? question) (+ score (:score question 0)) score)
                  :correct? correct?
                  :difficulty (:difficulty question)
                  :streak (if (:correct? question) (inc streak) 0)})))



(defn adjust-difficulty [{:keys [correct? difficulty] :as state
                          :or {correct? false difficulty :any}}]
  (let [difficulty (or difficulty :any)
        index (.indexOf difficulty-levels difficulty)
        new-index (if correct? (inc index) (dec index))]
    (if (< new-index 1)
      (merge state {:difficulty :trivial})
      (if (>= new-index (count difficulty-levels))
        (merge state {:difficulty :tricky})
        (merge state {:difficulty (get difficulty-levels new-index)})))))



(defn pick-next-question [{:keys [pool difficulty] :as state}]
  (let [question (->> pool
                      (filter #(= (:difficulty %) difficulty))
                      (rand-nth))
        pool (remove #(= (:id %) (:id question)) pool)
        quiz (conj (:quiz state) question)]
    (merge state {:pool pool
                  :quiz quiz
                  :next-question question})))



(defn fetch-next-question [{:keys [pool quiz score streak] :as state} {:keys [id answer] :as user-input}]
  (-> state
      (answer-question user-input)
      (adjust-difficulty)
      (pick-next-question)))
