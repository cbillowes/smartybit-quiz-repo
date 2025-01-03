(ns smartybit-quiz.questionnaire.entities
  (:require [malli.core :as m]
            [malli.error :as me]))


(def ^:private Attachment
  [:map
   [:media [:enum :image :video :audio]]
   [:text :string]
   [:url :string]])


(def ^:private Question
  [:map
   [:id :string]
   [:question :string]
   [:type [:enum "multiple" "single" "open" "number"]]
   [:answers [:vector [:map
                       [:id :string]
                       [:answer :string]
                       [:correct :boolean]
                       [:weight {:optional true} :int]
                       [:points {:optional true} :int]
                       [:difficulty {:optional true} [:enum :easy :medium :hard :tricky]]
                       [:attachment {:optional true} [:ref #'Attachment]]
                       [:elaboration {:optional true} :string]
                       [:references {:optional true} [:vector [:map
                                                               [:ref :string]
                                                               [:link :string]]]]]]]
   [:category {:optional true} :string]
   [:max-answers {:optional true} :int]
   [:hints {:optional true} [:vector :string]]
   [:timer {:optional true} :int]
   [:attachment {:optional true} [:ref #'Attachment]]
   [:tags {:optional true} [:vector :string]]
   [:sub-category {:optional true} :string]
   [:references {:optional true} [:vector [:map
                                           [:ref :string]
                                           [:link :string]]]]])


(def ^:private Questionnaire
  [:map
   [:id :uuid]
   [:slug :string]
   [:title :string]
   [:author :string]
   [:category :string]
   [:status [:enum "draft" "published" "archived"]]
   [:description {:optional true} :string]
   [:questions {:optional true} [:vector [:ref #'Question]]]
   [:private? {:optional true} :boolean]
   [:tags {:optional true} [:vector :string]]
   [:sub-category {:optional true} :string]
   [:references {:optional true} [:vector [:map
                                           [:ref :string]
                                           [:link :string]]]]])


(defn- valid-questionnaire?
  [questionnaire]
  (if (false? (m/validate Questionnaire questionnaire))
    (let [errors (m/explain Questionnaire questionnaire)]
      (throw (ex-info (format "Invalid questionnaire %s" (me/humanize errors)) {:errors errors})))
    true))


(defn serialize-questionnaire
  [data]
  (when (valid-questionnaire? data)
    data))
