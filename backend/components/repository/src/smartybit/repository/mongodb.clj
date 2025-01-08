(ns smartybit.repository.mongodb
  (:require [monger.core :as mg]
            [mount.core :refer [defstate]]))


(def ^:dynamic *uri* nil)


(defstate conn
  :start (:db (mg/connect-via-uri *uri*))
  :stop (mg/disconnect (:conn conn)))