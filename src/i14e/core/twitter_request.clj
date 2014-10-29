(ns i14e.core.twitter-request
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [oauth.client :as oauth]
            [clj-http.client :as client]
            [clojure.data.json :as json]) )
(defn say-hello [] "hello")
