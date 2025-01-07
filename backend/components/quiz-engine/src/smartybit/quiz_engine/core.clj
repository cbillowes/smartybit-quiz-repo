(ns smartybit.quiz-engine.core)

;; The :any difficulty level is a circular marker to move the difficulty levels
;; back to the beginning when the pool is depleted of questions of a certain difficulty.
(def ^:private difficulty-levels [:any :trivial :easy :medium :hard :tricky])


(defn current-time-in-millis []
  (System/currentTimeMillis))


(defn answer-question [{:keys [quiz score streak] :as state
                        :or {quiz [] score 0 streak 0}}
                       {:keys [id answer]}]
  (if answer
    (let [question (->> quiz
                        (filter #(= (:id %) id))
                        (first))
          correct?  (= (:answer question) answer)
          question (assoc question
                          :correct? correct?
                          :answer {:got answer :actual (:answer question)}
                          :answered-at (current-time-in-millis))
          score (if (:correct? question) (+ score (:score question 0)) score)]
      (merge state {:quiz (map #(if (= (:id %) id) question %) quiz)
                    :score score
                    :advance? correct?
                    :difficulty (:difficulty question)
                    :streak (if (:correct? question) (inc streak) 0)}))
    (merge state {:streak 0
                  :score 0
                  :difficulty (first difficulty-levels)})))



(defn get-next-difficulty [difficulty advance?]
  (let [difficulty (or difficulty (first difficulty-levels))
        index (.indexOf difficulty-levels difficulty)]
    (if advance?
      (if (>= index (dec (count difficulty-levels)))
        (get difficulty-levels 1) ;; skip the :any difficulty level
        (get difficulty-levels (inc index)))
      (if (<= index 1)
        (get difficulty-levels 1)
        (get difficulty-levels (dec index))))))


(defn adjust-difficulty [{:keys [pool advance? difficulty] :as state
                          :or {pool [] advance? false}}
                         & {:keys [difficulties]
                            :or {difficulties (rest difficulty-levels)}}]
  (let [next-difficulty (get-next-difficulty difficulty advance?)
        question-pool (filter #(= (:difficulty %) difficulty) pool)
        difficulties (filter #(not= next-difficulty %) difficulties)]
    (if (empty? question-pool)
      (if (empty? difficulties)
        (merge state {:difficulty difficulty})
        (adjust-difficulty (merge state {:difficulty next-difficulty})
                           :difficulties difficulties))
      (merge state {:difficulty difficulty}))))



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
      (pick-next-question)
      (assoc-in [:next-question :picked-at] (current-time-in-millis))))
