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

(deftemplate landing-page
  []
  [:div.docpage
   [:h2 "Welcome to " [:span.cloc-mono "cloc"]]
   [:p (str "Use the navigation bar on the left to browse around "
            "documentation for your own code and libraries you have "
            "on your project classpath.")]
   [:p (str "The search bar at the top will give you real-time search "
            "suggestions as you type. This is probably going to be the "
            "easiest way to find what you're looking for!")]
   [:p (str "To be honest, if you couldn't figure those few things out "
            "for yourself just by looking at the UI, you should probably "
            "just back away from the keyboard and forget about trying to "
            "to do any programming. I just wanted to write something in "
            "this space because the landing page looked empty!")]])

(defn ns-docs
  "Return a documentation page as a div for requested
   namespace and library."
  [lib namesp & [name]]
  (GET "/api/docs"
       {:params {:lib lib
                 :namespace namesp}
        :handler (fn [docs]
                   (swap-docpage (docpage-div lib docs))
                   (when name
                     (when-let [elem (.getElementById js/document (str name))]
                       (.scrollIntoView elem))))}))

(defn show-landing!
  []
  (swap-docpage (landing-page)))
