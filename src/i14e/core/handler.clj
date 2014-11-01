(ns i14e.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [i14e.core.template :as template]
            [i14e.core.twitter-request :as twitter]
            [clojure.data.json :as json] ) )

(def tokens {:oauth_token "958968890-VRVPgPJLezQdxPpWhHzWRP4ii6pa11BurFd4a2gt" :oauth_secret "v3zMT8YKFqD5sXeREnTJzm0nZsIP6NGB7jcQKN6OyAeKX" :user_id "958968890" })
(defroutes app-routes
  (GET "/cache" [] 
    (-> (twitter/twitter-request "https://api.twitter.com/1.1/friends/ids.json" {:user_id "958968890"} tokens (str "?user_id=958968890")) 
      (str)
      )) 
  (GET "/req/:screen_name/:language" [screen_name language location] 
    (twitter/user-hydrate 
      (take 100 (sort-by val > (-> (twitter/followers-of screen_name tokens)
       (twitter/lang-map tokens)
       (get language)
       (twitter/following-map tokens)
       (twitter/user-reduce) )))
      tokens)
    )
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
;; dan rob hiem? karim mitch