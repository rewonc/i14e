(ns i14e.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]))

(defn cn []
  "Returns a connection to the specified DB"
  (if (env) 
    (mg/connect-via-uri (env))
    (mg/get-db (mg/connect) "test") ))

(defn insert [cn coll first_name last_name]
    (mc/insert cn coll {:first_name first_name  :last_name last_name})
    (str first_name " " last_name))
        
(defn retrieve [cn, coll]
    (print-str (mc/find-maps cn coll))) 

(defn env [] 
  (get (System/getenv) "MONGOHQ_URL"))

(defroutes app-routes
  (GET "/" [] (str (env)))
  (GET "/get" [] (str (retrieve (cn) "documents")))
  (GET "/insert/:first_name/:last_name" 
    [first_name last_name] (insert (cn) "documents" first_name last_name))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

