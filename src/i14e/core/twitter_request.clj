(ns i14e.core.twitter-request
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [oauth.client :as oauth]
            [clj-http.client :as client]
            [clojure.data.json :as json])
  (:import [com.mongodb MongoOptions ServerAddress]))
(defn say-hello [] "hello")
