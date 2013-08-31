(ns cloc.browser
  "Library and namespace browser UI"
  (:use-macros
   [dommy.macros :only [sel1 sel node deftemplate]])
  (:require [dommy.core :as d]
            [ajax.core  :refer [GET]]
            [cloc.docpage :as docpage]))

(declare init!)

(defn with-listener
  [e f]
  (doto (node e)
    (d/listen! :click f)))

(defn code-list
  "Formated list for code"
  [items listener]
  (for [i (sort items)
        :let [item (str i)]]
    [:ul.nav.nav-list
     [:li.browser-item
      (with-listener [:a
                      [:i.icon-folder-close]
                      [:span.browser-item item]]
        listener)]]))

(defn switch-to!
  "Switch the browser content to the given node"
  [content]
  (when-let [current (sel1 :.browser-content)]
    (d/remove! current))
  (-> (sel1 :#browser-container)
      (d/append! (d/add-class! content :browser-content))))

(deftemplate lib-section
  [lib-name namespaces]
  [:div.lib-section
   [:div.lib-section-nav
    [:i {:class "icon-arrow-left"}]
    (with-listener [:a "Back to Libraries"]
      (fn [_] (init!)))]
   [:div.lib-heading lib-name]
   (code-list namespaces
              (fn [e]
                (let [namespace (str (.. e -target -textContent))]
                  (docpage/ns-docs lib-name namespace))))])

(deftemplate browser-section
  [title items]
  [:div.browser-section
   [:h4 title]
   (code-list items
              (fn [e]
                (let [lib (str (.. e -target -textContent))]
                  (GET "/api/ns"
                       {:params {:lib lib}
                        :handler (fn [nses]
                                   (switch-to! (lib-section lib nses)))}))))])

(deftemplate libs-list
  [dirs libs]
  [:div
   (browser-section "Local"     dirs)
   (browser-section "Libraries" libs)])

(defn init!
  []
  (GET
   "/api/local"
   {:handler (fn [dirs]
               (GET
                "/api/libs"
                {:handler (fn [libs]
                            (switch-to! (libs-list dirs libs)))}))}))
