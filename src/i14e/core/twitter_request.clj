(ns i14e.core.twitter-request
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [oauth.client :as oauth]
            [clj-http.client :as http]
            [clojure.data.json :as json]) )

(defn say-hello [] "hello")

(defn get-followers [id] 
  (["followers"]))

;; TODO: store secrets in env keys"
(def consumer (oauth/make-consumer "OHmILm1FOMKsohoKQdAdH61tX"
    "qYVAAXqm3348LNZzRkBYCKvThYXtixK7rW1dS96pYGjQdh3V7B"
    "https://api.twitter.com/oauth/request_token"
    "https://api.twitter.com/oauth/access_token"
    "https://api.twitter.com/oauth/authorize"
    :hmac-sha1))

(def request-token (oauth/request-token consumer "http://localhost:3000/auth/callback"))

(defn uri []
  (oauth/user-approval-uri consumer 
    (:oauth_token request-token))
)

;; Response: http://localhost:3000/auth/callback?oauth_token=heckef6fBrLV6tUSU9voaroBsL7h0Shx&oauth_verifier=VBG4qE992A9ffPbQMAmZ9LPKbtCNzn0K


(defn access-token-response [token verifier] (oauth/access-token 
  consumer 
  request-token
  verifier))
;;should save these

;;response: 
;;{:oauth_token "958968890-VRVPgPJLezQdxPpWhHzWRP4ii6pa11BurFd4a2gt", :oauth_token_secret "v3zMT8YKFqD5sXeREnTJzm0nZsIP6NGB7jcQKN6OyAeKX", :user_id "958968890", :screen_name "rewonfc"}
(def temporary-token {:oauth_token "958968890-VRVPgPJLezQdxPpWhHzWRP4ii6pa11BurFd4a2gt", :oauth_token_secret "v3zMT8YKFqD5sXeREnTJzm0nZsIP6NGB7jcQKN6OyAeKX", :user_id "958968890", :screen_name "rewonfc"})
;;https://api.twitter.com/1.1/friends/ids.json?screen_name=rewonfc

(defn credentials [tokens] (oauth/credentials consumer
  (:oauth_token tokens)
  (:oauth_token_secret tokens)
  :GET
  "https://api.twitter.com/1.1/friends/ids.json"
  {:screen_name "rewonfc"}))

(defn following [token] 
  (http/get "https://api.twitter.com/1.1/friends/ids.json?screen_name=rewonfc" 
    {:query-params (credentials token)})
  )

