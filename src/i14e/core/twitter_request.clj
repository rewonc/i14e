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

;; OAUTH KEY
;; TODO: store secrets in env keys"
(def consumer (oauth/make-consumer "OHmILm1FOMKsohoKQdAdH61tX"
    "qYVAAXqm3348LNZzRkBYCKvThYXtixK7rW1dS96pYGjQdh3V7B"
    "https://api.twitter.com/oauth/request_token"
    "https://api.twitter.com/oauth/access_token"
    "https://api.twitter.com/oauth/authorize"
    :hmac-sha1))


;; SIGNING REQUESTS
;; need to adjust params in the body
(defn sign [tokens url query] (oauth/credentials consumer
  (get tokens "oauth_token" )
  (get tokens "oauth_token_secret")
  :GET
  url
  query))

;;QUERIES
(defn cache-lookup [query] (mc/find-one-as-map (cn) "queries" {:query query} ) )
(defn cache-save [query result] (mc/insert (cn) "queries" {:query query :result result}) ) 

(defn twitter-request [url querymap token querystring] 
  "Execute a request for the given url and id, assuming ID is stored in DB"
  (let [cache (cache-lookup querystring)]
    (if (nil? cache) 
      (let [resp (-> (http/get (str url querystring) 
          {:query-params (sign token url querymap )})
          :body )]
        (cache-save querystring resp)
        (json/read-str resp))
      (json/read-str (:result cache)) ) ))
;;introduce caching here


(defn lang-map [users token] ;;180 rate limit, 100 ids max. we should do this 30 times. 
  (let [sample (subvec users 0 100) 
        commas (apply str (interpose "," sample))
        resp (twitter-request "https://api.twitter.com/1.1/users/lookup.json" {:user_id commas :include_entities false} token (str "?user_id=" commas "&include_entities=false"))]
        (->> 
          (map #(vector (get % "id") (get % "lang")) resp) 
          (reduce 
            (fn [coll [id lang]] (if (nil? (get coll lang)) (assoc coll lang [id]) (assoc coll lang (conj (get coll lang) id))))
            {}))
        ;;cache requests here so we dont have to do the same thing for other languages (or places...)
    ))

(defn lang-map-controller [users token]
  ;;if count is > 100, take first 100 and recur the rest
  ;;if count is < 100, go through with it
  ;;pass lang map between requests and conj it 
  
  ;;step 1: separate it into blocks of 100 ;; subvec v and v
  ;;step 2: let them all be reduced to lang maps
  ;;step 3: aggregate all the lang maps
  (let [sample (subvec users 0 100) 
        commas (apply str (interpose "," sample))]
  ))
(defn user-hydrate [users token] ;;180 rate limit, 100 ids max.
  (let [sample (vec users) 
        ids (map #(nth % 0) sample)
        commas (apply str (interpose "," ids))
        resp (twitter-request "https://api.twitter.com/1.1/users/lookup.json" {:user_id commas :include_entities false} token (str "?user_id=" commas "&include_entities=false"))]
        (str resp) ))


(defn user-following [id token] ;;15 rate limit ;; this will be bottleneck.
  ;;these should be async.
    (-> (twitter-request "https://api.twitter.com/1.1/friends/ids.json" {:user_id id} token (str "?user_id=" id)) 
      (get "ids")))


(defn following-map [users token] 
  ;maybe ask how many objs can be queried at this point.
  (map #(user-following % token) users))
  
  

(defn followers-of [screen_name token] ;;15 rate limit
    (-> (twitter-request "https://api.twitter.com/1.1/followers/ids.json" {:screen_name screen_name} token (str "?screen_name=" screen_name)) 
      (get "ids") ) )

(defn user-reduce [input] 
  (reduce 
    (fn [coll item] 
      (reduce (fn [subcoll ids] 
          (if (nil? (get subcoll ids )) (assoc subcoll ids 1)  (assoc subcoll ids (+ 1 (get subcoll ids))))
        ) coll item)
    )
    {} input))