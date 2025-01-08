(ns smartybit.webapp.api.profile
  (:require [io.pedestal.http.route :as route]
            [smartybit.profile.repository.core :as repo]
            [smartybit.profile.repository.mongodb :as profiles]))


(defn get-profiles [_]
  {:status 200 :content-type "application/json" :body (repo/get-profiles (profiles/profile-repository))})


(def routes #{["/profiles" :get get-profiles :route-name :profiles]})
