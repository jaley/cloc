(ns cloc.searchbar
  "Bootstrap navbar and search at top of screen."
  (:use-macros
   [dommy.macros :only [sel1 sel node deftemplate]])
  (:require [dommy.core :as d]
            [ajax.core  :refer [GET]]
            [cloc.docpage :as docpage]))

(defn- searchbar-div
  "Render the Navbar div"
  []
  [:div.navbar
   [:div.navbar-inner
    [:a.brand {:href "http://github.com/jaley/cloc"
               :target "_blank"}
     "CL"
     [:img.img-circle {:src "img/clojure-icon.gif"
                       :height 20
                       :width  20}]
     "C"]
    [:form.navbar-search.pull-right {:style "padding: 5px 20px 10px;"}
     [:input.search-query {:type "text" :placeholder "Search"}]]]])

(defn init!
  "Add the navbar to the document"
  []
  (-> (sel1 :#searchbar-container)
      (d/append! (searchbar-div))))
