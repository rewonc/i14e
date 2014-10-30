(ns i14e.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [i14e.core.template :as template]
            [i14e.core.twitter-request :as twitter] ) )

(defroutes app-routes
  (GET "/hello" [] (template/summary [:div.counts
    [:h5 "Some real-time queries of data"]
    [:p (str "Count one cursored result for followers of pmarca: " (-> (twitter/followers-of "pmarca" "958968890" ) 
      (get "ids") 
      (count) ))]
    ] ))
  (GET "/" [] (str (twitter/uri)))
  (GET "/req" [] (str (twitter/followers-of "pmarca" "958968890" )))
  (GET "/session" {session :session} 
    {:status 200 :body (str "session txt" session) :headers {"Content-Type" "text/html"} :session (assoc session :hello "world") })
  (GET "/auth/callback" {params :query-params session :session} 
    (let [auth (twitter/access-token-response (get params "oauth_token") (get params "oauth_verifier"))]
      {:status 200 :body (str "user saved to session: " (:screen_name auth)) :headers {"Content-Type" "text/html"} :session (assoc session :user_id (:user_id auth) ) }))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
