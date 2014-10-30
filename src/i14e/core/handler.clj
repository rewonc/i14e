(ns i14e.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [i14e.core.template :as template]
            [i14e.core.twitter-request :as twitter]
            [clojure.data.json :as json] ) )

(def tokens {:oauth_token "958968890-VRVPgPJLezQdxPpWhHzWRP4ii6pa11BurFd4a2gt" :oauth_secret "v3zMT8YKFqD5sXeREnTJzm0nZsIP6NGB7jcQKN6OyAeKX" :user_id "958968890" })

(defroutes app-routes
 
  (GET "/req/:screen_name/:language" [screen_name language location] 
    (-> (twitter/followers-of screen_name tokens)
      (get "ids")
      (twitter/user-lookup tokens language)
      ;(twitter/user-populate tokens)
      ;(twitter/user-reduce)
      str))
  
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))

;;;;
;Access Token  958968890-VRVPgPJLezQdxPpWhHzWRP4ii6pa11BurFd4a2gt
;Access Token Secret v3zMT8YKFqD5sXeREnTJzm0nZsIP6NGB7jcQKN6OyAeKX
;Access Level  Read-only
;Owner rewonfc
;Owner ID  958968890
;  )
;;;;