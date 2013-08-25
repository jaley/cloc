(ns cloc.pages
  (:require [hiccup
             [core    :as h]
             [page    :as p]]))

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
    [:body {:onload (str "cloc.core.load_ui(); "
                         (if debug? "cloc.repl.connect()" ""))}
     [:div {:class "container"}
      [:div {:id "searchbar-container"}]
      [:div {:class "row"}
       [:div {:id "browser-container"
              :class "span3"}]
       [:div {:id "docpage-container"
              :class "span9"}]]]]]))
