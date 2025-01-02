(ns smartybit.quiz-engine.core)


(defn fetch-next-question
  [questions & {:keys [index direction]}]
  (let [next-index (if (= direction :forward)
                     (inc index)
                     (dec index))]
    (get questions next-index)))
