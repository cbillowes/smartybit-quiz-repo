(ns smartybit.profile.repository.mongodb
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [mount.core :refer [defstate]]
            [smartybit.profile.repository.core :refer [ProfileRepository] :as repository]))


(def ^:private collection-name "profiles")


(defrecord MongoProfileRepository [conn]
  ProfileRepository

  (get-profiles
   [this]
   (mc/find-maps conn collection-name))

  (get-profile
   [this id]
   (mc/find-map-by-id conn collection-name id))

  (create-profile
   [this profile]
   (mc/insert conn collection-name profile))

  (update-profile
    [this id profile]
    (mc/update conn collection-name {:_id id} profile))

  (delete-profile
    [this id]
    (mc/remove conn collection-name {:_id id})))

;; DB will come from a config. Right now it's just using silly values hardcoded in docker.
(defn profile-repository []
  (->MongoProfileRepository
   (:db (mg/connect-via-uri "mongodb://myuser:mypassword@localhost:27017/smartybit-profiles?authSource=admin"))))


(comment

  (repository/create-profile (profile-repository) {:name "John Doe"})

  (repository/get-profiles (profile-repository))

  )

(defstate conn
  :start (let [uri (System/getenv "MONGO_URI")]
           (:db (mg/connect-via-uri "mongodb://localhost:27017/mongo")))
  :stop (mg/disconnect (:conn conn)))