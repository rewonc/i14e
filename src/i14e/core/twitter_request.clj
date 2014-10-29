(ns i14e.core.twitter-request
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [oauth.client :as oauth]
            [clj-http.client :as client]
            [clojure.data.json :as json]) )
(defn say-hello [] "hello")
(defn get-followers [id] 
  (["followers"]))

(defn uri []
  (def consumer (oauth/make-consumer "OHmILm1FOMKsohoKQdAdH61tX"
    "qYVAAXqm3348LNZzRkBYCKvThYXtixK7rW1dS96pYGjQdh3V7B"
    "https://api.twitter.com/oauth/request_token"
    "https://api.twitter.com/oauth/access_token"
    "https://api.twitter.com/oauth/authorize"
    :hmac-sha1))

  (def request-token (oauth/request-token consumer "http://localhost:3000"))
  (oauth/user-approval-uri consumer 
                             (:oauth_token request-token))
)


