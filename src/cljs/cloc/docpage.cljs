(ns cloc.docpage
  "Generate documentation pages"
  (:use-macros
   [dommy.macros :only [sel1 sel node deftemplate]])
  (:require [clojure.string :as str]
            [dommy.core :as d]
            [ajax.core  :refer [GET]]))

(defn- swap-docpage
  [new-docs]
  (when-let [current (sel1 :.docpage)]
    (d/remove! current))
  (-> (sel1 :#docpage-container)
      (d/append! new-docs)))

(deftemplate docpage-div
  [lib doc-map]
  (let [publics (sort-by :name (:publics doc-map))]
   [:div.docpage
    [:div.row
     [:h2.cloc-mono (str (get doc-map :name))]
     [:hr]
     [:div.span5
      [:dl.dl-horiztonal
       [:dt "Library"] [:dd.cloc-mono lib]
       [:dt "Author"]  [:dd (get doc-map :author "Unknown")]]]
     [:div.span3
      [:h4 "Public Vars"]
      [:ul.unstyled
       (for [{:keys [name]} publics
             :let [name (str name)]]
         [:li.cloc-mono [:a {:href (str "#" name)} name]])]]]
    [:hr]
    (for [v publics
          :let [varname (str (:name v))]]
      [:div.row {:id varname}
       [:span.var-heading varname]
       [:div
        [:span.cloc-mono (str/join ", " (map pr-str (:arglists v)))]]
       [:div.var-doc
        [:pre (:doc v)]]])]))

(defn ns-docs
  "Return a documentation page as a div for requested
   namespace and library."
  [lib namesp]
  (GET "/api/docs"
       {:params {:lib lib
                 :namespace namesp}
        :handler (fn [docs]
                   (swap-docpage (docpage-div lib docs)))}))
