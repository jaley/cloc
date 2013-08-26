(ns cloc.searchbar
  "Bootstrap navbar and search at top of screen."
  (:use-macros
   [dommy.macros :only [sel1 sel node deftemplate]])
  (:require [dommy.core :as d]
            [ajax.core  :refer [GET]]
            [cloc.docpage :as docpage]))

(defn hide-results!
  []
  (-> (sel1 :#results-list)
      (d/hide!)))

(defn open-docs!
  [item]
  (let [lib       (d/attr item :_lib)
        namespace (d/attr item :_namespace)
        name      (d/attr item :_name)]
    (hide-results!)
    (docpage/ns-docs lib namespace name)))

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
     [:input#search-field.search-query
      {:type "text" :placeholder "Search"}]
     [:ul#results-list {:style "display: none;"}]]]])

(defn shorten
  "Shorten string s to n chars."
  [s n]
  (let [short (.substring s 0 n)]
    (if (< (.-length short) (.-length s))
      (str short "...")
      short)))

(defn parent-result
  [e]
  (d/closest (.-srcElement e) :li.result))

(defn with-results-listeners
  [list-item]
  (doto (node list-item)
    (d/listen! :mouseenter
               (fn [e]
                 (when-let [current (sel1 :.active-result)]
                   (d/remove-class! current :active-result))
                 (d/add-class! (parent-result e) :active-result))

               :click
               (fn [e]
                 (open-docs! (parent-result e))))))

(defn make-results-list
  [results]
  [:ul#results-list.unstyled
   (for [{:keys [lib namespace name docs]} results]
     (with-results-listeners
       [:li.result {:_lib       (str lib)
                    :_namespace (str namespace)
                    :_name      (str name)}
       [:span.cloc-mono [:b (str namespace "/" name)]] [:br]
       [:span.cloc-mono [:i (shorten docs 140)]]]))])

(defn update-results!
  [results]
  (when-let [results (seq results)]
   (-> (sel1 :#results-list)
       (d/replace! (make-results-list results))
       (d/show!))
   (d/add-class! (sel1 :.result) :active-result)))

(defn search!
  [e]
  (let [qry (-> (sel1 :#search-field) d/value)]
    (if (empty? qry)
      (hide-results!)
      (GET "/api/search"
           {:params  {:query qry}
            :handler update-results!}))))

(defn init!
  "Add the navbar to the document"
  []
  (-> (sel1 :#searchbar-container)
      (d/append! (searchbar-div)))
  (d/listen! (sel1 :#search-field) :input search!))
