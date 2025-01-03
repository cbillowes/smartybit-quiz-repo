(ns smartybit.quiz-engine.next-question)














;;
;; (defn- validate
;;   [{:keys [answer explanation score]
;;     :or {answer nil explanation "" score 0}} user-input]
;;   (let [correct? (= user-input answer)]
;;     {:correct? correct?
;;      :explanation explanation
;;      :score (if correct? score 0)
;;      :answer answer}))


;; (defn- get-next-difficulty
;;   [difficulty]
;;   (let [index (.indexOf difficulty-levels difficulty)]
;;     (if (= index (dec (count difficulty-levels)))
;;       difficulty
;;       (get difficulty-levels (inc index)))))


;; (defn- get-previous-difficulty
;;   [difficulty]
;;   (let [difficulty-levels (rest difficulty-levels) ;; removes the :any marker
;;         index (.indexOf difficulty-levels difficulty)]
;;     (if (= index 1)
;;       difficulty
;;       (get difficulty-levels (dec index)))))


;; (defn- get-questions-by-difficulty
;;   [questions difficulty correct?]
;;   (let [pool (filter #(= (:difficulty %) difficulty) questions)]
;;     (println difficulty correct?)
;;     (if (and (= difficulty (last difficulty-levels))
;;              (empty? pool))
;;       (get-questions-by-difficulty questions :any correct?)
;;       (if (and (not (empty? questions))
;;                (empty? pool))
;;         (get-questions-by-difficulty questions (if correct?
;;                                                  (get-next-difficulty difficulty)
;;                                                  (get-previous-difficulty difficulty)) correct?)
;;         pool))))


;; (defn fetch-next-question
;;   [question-pool & {:keys [answers current-index]}]
;;   (let [answer (get answers current-index)
;;         question (->> question-pool
;;                       (filter #(= (:id %) (:id answer)))
;;                       (first))
;;         correct? (validate question (:answer answer))]
;;     (clojure.pprint/pprint
;;      {:question-pool question-pool
;;       :answers (conj answers correct?)
;;       :current-index (inc current-index)
;;       :question (if correct?
;;                   (get-next-difficulty (:difficulty question))
;;                   (get-previous-difficulty (:difficulty question)))})))

;;   #_(let [quiz-question (get quiz index)
;;         pool-question (->> question-pool
;;                            (filter #(not= (:id %) (:id quiz-question)))
;;                            (first))
;;         correct? (->> (validator/validate-answer pool-question (:answer quiz-question))
;;                       (:correct?))
;;         difficulty (:difficulty pool-question)
;;         ids-to-remove (set (map :id quiz))
;;         pool (filter #(not (contains? ids-to-remove (:id %))) question-pool)
;;         pool (get-questions-by-difficulty pool difficulty correct?)]
;;     (rand-nth pool)))


;; (comment

;;   (get-next-difficulty :trivial)

;;   (fetch-next-question
;;    [{:id 0 :text "What is the capital of Sweden?" :difficulty :trivial :answer "Stockholm"}
;;     {:id 1 :text "What is the capital of France?" :difficulty :trivial :answer "Paris"}
;;     {:id 2 :text "What is the capital of Mauritius?" :difficulty :trivial :answer "Port Louis"}
;;     {:id 3 :text "What is the capital of Germany?" :difficulty :trivial :answer "Berlin"}
;;     {:id 4 :text "What is the capital of Italy?" :difficulty :trivial :answer "Rome"}
;;     {:id 5 :text "What is the capital of Japan?" :difficulty :trivial :answer "Tokyo"}
;;     {:id 6 :text "What is the capital of Australia?" :difficulty :trivial :answer "Canberra"}
;;     {:id 7 :text "What is the capital of Brazil?" :difficulty :easy :answer "Brasília"}]
;;    :answers [{:id 0 :answer "Stockholm"}]
;;    :current-index 0)

;;   )
