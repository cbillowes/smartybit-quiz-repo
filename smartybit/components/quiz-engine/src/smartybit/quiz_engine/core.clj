(ns smartybit.quiz-engine.core)


(defn fetch-next-question
  [questions & {:keys [index]}]
  (let [next-index (inc index)]
    (get questions next-index)))
