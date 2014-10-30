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
;; need to adjust params in the body
(defn sign [tokens url query] (oauth/credentials consumer
  (get tokens "oauth_token" )
  (get tokens "oauth_token_secret")
  :GET
  url
  query))

(defn token-lookup [id] (mc/find-one (cn) "tokens" {:user_id id} ) )

;;TODO: fail for failed lookup
(defn twitter-request [url querymap id querystring] 
  "Execute a request for the given url and id, assuming ID is stored in DB"
  (let [token (token-lookup id) ]
    (-> (http/get (str url querystring) 
      {:query-params (sign token url querymap )})
      :body 
      json/read-str)
    ))

(defn get-followers [id] 
  (twitter-request "https://api.twitter.com/1.1/friends/ids.json" {:user_id id} id (str "?user_id=" id)) )
(defn user-lookup [users id] 
    (twitter-request "https://api.twitter.com/1.1/users/lookup.json" {:user_id users} id (str "?user_id=" users)) )
(defn followers-of [screen_name id] 
    (twitter-request "https://api.twitter.com/1.1/followers/ids.json" {:screen_name screen_name} id (str "?screen_name=" screen_name)) )
(defn user-following [screen_name id] 
    (twitter-request "https://api.twitter.com/1.1/friends/ids.json" {:screen_name screen_name} id (str "?screen_name=" screen_name)) )
