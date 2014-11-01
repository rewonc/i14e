(ns i14e.core.async-test
  (:require [org.httpkit.client :as http] ))

;;(:require [i14e.core.async-test :refer :all])

;;this is doing a get request. Then it is

(let [resp1 (http/get "http://http-kit.org/")
      resp2 (http/get "http://clojure.org/")
      resp3 (http/get "https://google.com/")
      resp4 (http/get "http://facebook.com/")
      resp5 (http/get "http://yahoo.com/")
      resp6 (http/get "http://microsoft.com/")]
  (println "Response 1's status: " (:status @resp1)) ; wait as necessary
  (println "Response 2's status: " (:status @resp2))
  (println "Response 3's status: " (:status @resp3))
  (println "Response 4's status: " (:status @resp4))
  (println "Response 5's status: " (:status @resp5))
  (println "Response 6's status: " (:status @resp6))
  (println "Response 1's status: " (:body @resp1)) ; wait as necessary
  (println "Response 2's status: " (:body @resp2))
  (println "Response 3's status: " (:body @resp3))
  (println "Response 4's status: " (:body @resp4))
  (println "Response 5's status: " (:body @resp5))
  (println "Response 6's status: " (:body @resp6))
  )