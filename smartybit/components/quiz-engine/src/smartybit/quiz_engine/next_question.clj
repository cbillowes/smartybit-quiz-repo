(ns smartybit.quiz-engine.next-question)


(def ^:private difficulty-levels [:any :trivial :easy :medium :hard :tricky])


(defn- get-next-difficulty
  [difficulty]
  (let [index (.indexOf difficulty-levels difficulty)]
    (if (= index (dec (count difficulty-levels)))
      difficulty
      (get difficulty-levels (inc index)))))


(defn- get-questions-by-difficulty
  [questions difficulty]
  (let [pool (filter #(= (:difficulty %) difficulty) questions)]
    (if (and (= difficulty (last difficulty-levels))
             (empty? pool))
      (get-questions-by-difficulty questions :any)
      (if (and (not (empty? questions))
               (empty? pool))
        (get-questions-by-difficulty questions (get-next-difficulty difficulty))
        pool))))


(defn fetch-next-question
  [questions & {:keys [questionnaire difficulty]}]
  (let [ids-to-remove (set (map :id questionnaire))
        pool (filter #(not (contains? ids-to-remove (:id %))) questions)
        pool (get-questions-by-difficulty pool difficulty)]
    (first pool)))


(comment

  (fetch-next-question
   [{:id 0 :text "What is the capital of Sweden?" :difficulty :trivial}
    {:id 1 :text "What is the capital of France?" :difficulty :trivial}
    {:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
    {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
    {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
    {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
    {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
    {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}]
   :questionnaire [{:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                   {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
                   {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
                   {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
                   {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
                   {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}]
   :difficulty :easy)


  (fetch-next-question
   [{:id 0 :text "What is the capital of Sweden?" :difficulty :trivial}
    {:id 1 :text "What is the capital of France?" :difficulty :trivial}
    {:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
    {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
    {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
    {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
    {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
    {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}]
   :questionnaire [{:id 0 :text "What is the capital of Sweden?" :difficulty :trivial}
                   {:id 1 :text "What is the capital of France?" :difficulty :trivial}
                   {:id 2 :text "What is the capital of Mauritius?" :difficulty :easy}
                   {:id 3 :text "What is the capital of Germany?" :difficulty :easy}
                   {:id 4 :text "What is the capital of Italy?" :difficulty :medium}
                   {:id 5 :text "What is the capital of Japan?" :difficulty :hard}
                   {:id 6 :text "What is the capital of Australia?" :difficulty :tricky}
                   {:id 7 :text "What is the capital of Brazil?" :difficulty :tricky}]
   :difficulty :easy)

  )
