(ns i14e.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [i14e.core.template :as template]
            [i14e.core.twitter-request :as twitter]
            [clojure.data.json :as json] ) )

(defroutes app-routes
  (GET "/multi/:screen_name/:language" {params :params} 
    (let [tokens {:oauth_token (:oauth_token params) :oauth_secret (:oauth_secret params)} screen_name (:screen_name params) language (:language params)]

     {:status 200 :headers {"content-type" "text-json"} :body 
      (json/write-str  
        (into []
         (twitter/filter-users-by-language 
          (twitter/user-hydrate
            (take 100 (sort-by val > (-> (twitter/followers-of screen_name tokens)
              (twitter/lang-map-controller tokens language)
              (twitter/throttled-following-map tokens)
              (twitter/user-reduce) )))
            tokens) 
            language) )) }) )
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))


