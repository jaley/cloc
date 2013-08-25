(ns cloc.pages
  (:require [hiccup
             [core    :as h]
             [page    :as p]]))

(defn- navbar
  "Render the Navbar div"
  []
  [:div {:class "navbar"}
   [:div {:class "navbar-inner"}
    [:a {:class "brand" :href "/"}
     "CL"
     [:img {:class "img-circle"
            :src "img/clojure-icon.gif"
            :height 20
            :width  20}]
     "C"]
    [:form {:class "navbar-search pull-right"
            :style "padding: 5px 20px 10px;"}
     [:input {:type "text"
              :class "search-query"
              :placeholder "Search"}]]]])

(defn landing
  "Render the main landing page placeholder. Debug flag
   will connect up the browser-repl and use the unoptimized
   js file."
  [debug?]
  (h/html
   [:html
    [:head
     [:title "Cloc - Classpath Docs"]
     (p/include-css "css/bootstrap.css")
     (p/include-css "css/cloc.css")
     (if debug?
       (p/include-js "js/main-debug.js")
       (p/include-js "js/main.js"))]
    [:body {:onload (if debug? "cloc.repl.connect()" "")}
     [:div {:class "container"}
      (navbar)
      [:div {:class "row"}
       [:div {:id "browser-container"
              :class "span4"}]
       [:div {:id "docpage-container"
              :class "span8"}]]]]]))
