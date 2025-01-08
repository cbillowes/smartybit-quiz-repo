(ns smartybit.webapp.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [mount.core :as mount]
            [smartybit.repository.mongodb :as mongodb]
            [smartybit.profile.repository.core :as repo]
            [smartybit.profile.repository.mongodb :as profile-repo]))

(defn respond-hello [request]
  {:status 200 :content-type "application/json" :body (repo/get-profiles (profile-repo/profile-repository))})


(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello :route-name :greet]}))


(defn start []
  (binding [mongodb/*uri* "mongodb://myuser:mypassword@localhost:27017/smartybit-profiles?authSource=admin"]
    (mount/start #'mongodb/conn))

  (-> {::http/routes routes
       ::http/port 8890
       ::http/type :jetty}
      (http/create-server)
      (http/start)))


(defn stop []
  (mount/stop #'mongodb/conn))

(start)