(ns smartybit.webapp.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [mount.core :as mount]
            [smartybit.profile.repository.mongodb :as repository]))

(defn respond-hello [request]
  {:status 200 :body "Hello, world!"})


(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello :route-name :greet]}))


(defn start []
  (mount/start #'repository/conn)
  (-> {::http/routes routes
       ::http/port 8890
       ::http/type :jetty}
      (http/create-server)
      (http/start)))

