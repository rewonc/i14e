(ns i14e.core.twitter-request
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [oauth.client :as oauth]
            [clj-http.client :as http]
            [clojure.data.json :as json]) )

;; MONGODB ADAPTER
(defn env [] 
  (get (System/getenv) "MONGOHQ_URL"))

(defn cn []
  "Returns a connection to the specified DB"
  (if (env) 
    ( (mg/connect-via-uri (env)) :db )
    (mg/get-db (mg/connect) "test") ))

;; OAUTH PROCESSES
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

;; CALLBACK PROCESS
(defn access-token-response [token verifier] 
  (let [access_tokens (oauth/access-token consumer request-token verifier)]
    ;;TODO: perform validation checks on access_tokens here
    ;;store to db
    (def record (mc/insert (cn) "tokens" {:oauth_token (:oauth_token access_tokens) :oauth_token_secret (:oauth_token_secret access_tokens) :user_id (:user_id access_tokens) :screen_name (:screen_name access_tokens) }) )   ;;add to session
    
    access_tokens ) ) 

;; SIGNING REQUESTS

(defn sign [tokens url] (oauth/credentials consumer
  (:oauth_token tokens)
  (:oauth_token_secret tokens)
  :GET
  url
  {:user_id (:user_id tokens)}))

(defn twitter-request [url id] 
  "Execute a request for the given url and id, assuming ID is stored in DB"
  (let [tokens (mc/find-one (cn) "tokens" {:user_id id} ) ]
    (str (http/get url {:query-params (sign tokens "https://api.twitter.com/1.1/friends/ids.json")} )))
  )

(def temporary-token {:oauth_token "958968890-VRVPgPJLezQdxPpWhHzWRP4ii6pa11BurFd4a2gt", :oauth_token_secret "v3zMT8YKFqD5sXeREnTJzm0nZsIP6NGB7jcQKN6OyAeKX", :user_id "958968890", :screen_name "rewonfc"})

(defn get-followers [id] 
  ;;twitter request function
  (["followers"]))
(defn user-lookup [id] 
  (["followers"]))
(defn followers-of [id] 
  (["followers"]))

(defn following [token] 
  (http/get "https://api.twitter.com/1.1/friends/ids.json?user_id=958968890" 
    {:query-params (sign token "https://api.twitter.com/1.1/friends/ids.json")})
  )





;; Response: http://localhost:3000/auth/callback?oauth_token=heckef6fBrLV6tUSU9voaroBsL7h0Shx&oauth_verifier=VBG4qE992A9ffPbQMAmZ9LPKbtCNzn0K



  ;;response:
;;{:oauth_token "958968890-VRVPgPJLezQdxPpWhHzWRP4ii6pa11BurFd4a2gt", :oauth_token_secret "v3zMT8YKFqD5sXeREnTJzm0nZsIP6NGB7jcQKN6OyAeKX", :user_id "958968890", :screen_name "rewonfc"}
;;https://api.twitter.com/1.1/friends/ids.json?screen_name=rewonfc






