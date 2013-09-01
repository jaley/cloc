(ns cloc.routes
  (:require [compojure.core         :refer [defroutes routes GET context]]
            [compojure.route        :as    route]
            [ring.util.response     :as    resp]
            [ring.middleware.edn    :refer [wrap-edn-params]]
            [ring.middleware.params :refer [wrap-params]]
            [cloc.pages             :as    pages]
            [cloc.indexer           :as    ind]
            [cloc.index             :refer [index]]
            [cloc.search            :refer [search]]))

(defn landing-page
  "Generate a landing page response.
   debug? - if true, enable a ClojureScript REPL."
  [debug?]
  (-> (resp/response (pages/landing debug?))
      (resp/content-type "text/html")
      (resp/status 200)))

(defn edn-response
  "Generate an edn response for API functions."
  [data & [status]]
  (-> (resp/response     (pr-str data))
      (resp/status       (or status 200))
      (resp/content-type "application/edn")))

(defroutes pages
  (GET "/"      [] (landing-page false))
  (GET "/debug" [] (landing-page true))
  (route/resources "/"))

(defroutes api
  (context "/api" []
           (GET "/local" [] (edn-response (ind/local-code @index)))
           (GET "/libs"  [] (edn-response (ind/libraries @index)))
           (GET "/ns" [lib]
                (edn-response (ind/namespaces @index lib)))
           (GET "/docs" [lib namespace]
                (edn-response (ind/docs @index lib namespace)))
           (GET "/search" [query]
                (edn-response (search query)))))

(def main
  (-> (routes pages api)
      (wrap-edn-params)
      (wrap-params)))
