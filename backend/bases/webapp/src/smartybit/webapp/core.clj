(ns smartybit.webapp.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [mount.core :as mount]
            [smartybit.repository.mongodb :as mongodb]
            [smartybit.webapp.api.profile :as profile]))

(defonce *http-server (atom nil))


(def routes
  (route/expand-routes
   profile/routes))


(defn start []
  (binding [mongodb/*uri* "mongodb://myuser:mypassword@localhost:27017/smartybit-profiles?authSource=admin"]
    (mount/start #'mongodb/conn))

  (-> {::http/routes routes
       ::http/port 8890
       ::http/type :jetty}
      (http/create-server)
      (http/start)
      (#(reset! *http-server %))))


(defn stop []
  (mount/stop #'mongodb/conn)
  (when @*http-server
    (http/stop @*http-server)
    (reset! *http-server nil)))
