(ns smartybit.quiz-engine.core)

;; The :any difficulty level is a circular marker to move the difficulty levels
;; back to the beginning when the pool is depleted of questions of a certain difficulty.
(def ^:private difficulty-levels [:any :trivial :easy :medium :hard :tricky])


(defn- score-question [{:keys [pool quiz index user-question] :as state}]
  (let [answered-question (get quiz index)
        pool-question (->> pool (filter #(= (:id %) (:id answered-question))) first)
        answered-quiz (assoc quiz index (assoc answered-question
                                               :answer (:answer pool-question)
                                               :correct? (= (:answer pool-question) (:answer user-question))))]
    (merge state
           {:user-question user-question
            :quiz (if answered-question answered-quiz quiz)
            :pool (remove #(= user-question %) pool)})))


(defn- adjust-difficulty [difficulty crank-up?]
  (let [index (.indexOf difficulty-levels difficulty)]
    (if (> index (dec (count difficulty-levels)))
      (get difficulty-levels 0)
      (get difficulty-levels (if crank-up? (inc index) (dec index))))))


(defn- filter-by-difficulty [{:keys [pool quiz index]}]
  (let [difficulty (if (empty? quiz)
                     :trivial
                     (:difficulty (last quiz)))
        next-questions (filter #(= (:difficulty %) difficulty) pool)]
    {:pool pool :next-questions next-questions :quiz quiz :index index}))


(defn- get-unique-questions [{:keys [pool quiz index]}]
  (let [question (rand-nth pool)
        quiz (conj quiz question)
        pool (remove #(= question %) pool)]
    {:quiz quiz :pool pool :index index}))


(defn next-question [{:keys [pool quiz index user-question] :as state
                      :or {pool [] quiz [] index 0}}]
  (->> state
       (score-question)
       (filter-by-difficulty)
       (get-unique-questions)))



(clojure.pprint/pprint
 (next-question
  {:pool [{:id 0 :text "What is the capital of Sweden?" :difficulty :trivial :answer "Stockholm"}
          {:id 2 :text "What is the capital of Mauritius?" :difficulty :easy :answer "Port Louis"}
          {:id 3 :text "What is the capital of Germany?" :difficulty :easy :answer "Berlin"}
          {:id 6 :text "What is the capital of Australia?" :difficulty :tricky :answer "Canberra"}
          {:id 4 :text "What is the capital of Italy?" :difficulty :medium :answer "Rome"}
          {:id 5 :text "What is the capital of Japan?" :difficulty :hard :answer "Tokyo"}
          {:id 8 :text "What is the capital of France?" :difficulty :trivial :answer "Paris"}
          {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky :answer "Brasília"}]
   :quiz []
   :index 0
   :user-question nil})


 )
