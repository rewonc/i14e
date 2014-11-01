(defproject i14e "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [compojure "1.2.0"]
                 [ring/ring-defaults "0.1.2"]
                 [com.novemberain/monger "2.0.0"]
                 [hiccup "1.0.5"]
                 [http-kit "2.1.18"]
                 [clj-oauth "1.5.1"]
                 [clj-http "1.0.0"]
                 [org.clojure/data.json "0.2.5"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler i14e.core.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
