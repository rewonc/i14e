(ns i14e.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [oauth.client :as oauth]
            [clj-http.client :as client]
            [i14e.core.template :as template]
            [i14e.core.twitter-data :as tdata]
            [i14e.core.twitter-request :as twitter] ) )
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

(defroutes app-routes
  (GET "/" [] (template/summary [:div.counts
                          [:h5 "Counts"]
                          [:p (str "Rewon-following: " (count (get (get tdata/data :rewon-following) "ids")))]
                          [:p (str "pmarca-followers: " (count (get (get tdata/data :followers-of-pmarca) "ids")))]
                          [:p (str "pmarca-followers-details: " (count (get tdata/data :followers-of-pmarca-details) ))]
                          [:p (str "Rewon-following-details: " (count (get tdata/data :user-detail) ))]
                          [:p (str "Steps: " "Count Rewon-Following. Run following-details against all following (drawing from cache if necessary). Now, get the followers details of a")]] ))
  (GET "/get" [] (str (retrieve (cn) "tokens")))
  (GET "/hello" [] (str (twitter/uri)))

  (GET "/req" [] (str (twitter/twitter-request "https://api.twitter.com/1.1/friends/ids.json?user_id=958968890" "958968890")))
  
  (GET "/session" {session :session} 
    {:status 200 :body (str "session txt" session) :headers {"Content-Type" "text/html"} :session (assoc session :hello "world") })
  (GET "/following" [] (str (twitter/following twitter/temporary-token)))
  (GET "/insert/:first_name/:last_name" 
    [first_name last_name] (insert (cn) "documents" first_name last_name))
  (GET "/auth/callback" {params :query-params session :session} 
    (let [auth (twitter/access-token-response (get params "oauth_token") (get params "oauth_verifier"))]
      {:status 200 :body (str "user saved to session: " (:screen_name auth)) :headers {"Content-Type" "text/html"} :session (assoc session :user_id (:user_id auth) ) }))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
