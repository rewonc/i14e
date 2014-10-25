(ns i14e.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [hiccup.page :as hp])
  (:import [com.mongodb MongoOptions ServerAddress]))

(defn env [] 
  (get (System/getenv) "MONGOHQ_URL"))

(defn cn []
  "Returns a connection to the specified DB"
  (if (env) 
    ( (mg/connect-via-uri (env)) :db )
    (mg/get-db (mg/connect) "test") ))

(defn insert [cn coll first_name last_name]
    (mc/insert cn coll {:first_name first_name  :last_name last_name})
    (str first_name " " last_name))
        
(defn retrieve [cn, coll]
    (print-str (mc/find-maps cn coll))) 

(defn template [body] 
  (hp/html5 
    {:lang "en"}    
    [:meta {:charset "utf-8"}]
    [:title "i14n"]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:link {:href "css/vendor/bootstrap.min.css" :rel "stylesheet"}]
    [:link {:href "css/flat-ui.css" :rel "stylesheet"}]
    [:link {:href "img/favicon.ico" :rel "shortcut icon"}]
    [:head "<!--[if lt IE 9]>
      <script src=\"js/vendor/html5shiv.js\"></script>
      <script src=\"js/vendor/respond.min.js\"></script>
    <![endif]-->"]
    [:div.container
      [:p "This is some kind of intro"]
      [:h2 "hello world"] 
      body
     ]

    (hp/include-js "js/vendor/jquery.min.js")
    (hp/include-js "js/flat-ui.min.js")
   ))

(defroutes app-routes
  (GET "/" [] (template [:p "body"]))
  (GET "/get" [] (str (retrieve (cn) "documents")))
  (GET "/insert/:first_name/:last_name" 
    [first_name last_name] (insert (cn) "documents" first_name last_name))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

