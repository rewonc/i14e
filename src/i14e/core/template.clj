(ns i14e.core.template 
  (:require [hiccup.page :as hp]) )

(defn summary [body] 
  (hp/html5 
    {:lang "en"}    
    [:meta {:charset "utf-8"}]
    [:title "i14n"]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:link {:href "css/vendor/bootstrap.min.css" :rel "stylesheet"}]
    [:link {:href "css/flat-ui.css" :rel "stylesheet"}]
    [:link {:href "img/favicon.ico" :rel "shortcut icon"}]
    [:head "<!--[if lt IE 9]>
      <script src=\"js/vendor/html5shiv.js\"></script>
      <script src=\"js/vendor/respond.min.js\"></script>
    <![endif]-->"]
    [:div.container
      [:h2 "Internationalize your Twitter feed"] 
      body
     ]

    (hp/include-js "js/vendor/jquery.min.js")
    (hp/include-js "js/flat-ui.min.js")
   ))