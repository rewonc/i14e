(ns i14e.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]))

(defn cn 
  "Returns a connection to the specified DB"
  ([] (mg/get-db (mg/connect) "test"))
  ([db] (mg/get-db (mg/connect) db)))

(defn insert [cn coll first_name last_name]
    (mc/insert cn coll {:first_name first_name  :last_name last_name})
    (str first_name " " last_name))
        
(defn retrieve [cn, coll]
    (print-str (mc/find-maps cn coll))) 

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/get" [] (str (retrieve (cn) "documents")))
  (GET "/insert/:first_name/:last_name" 
    [first_name last_name] (insert (cn) "documents" first_name last_name))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

