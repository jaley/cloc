(ns cloc.routes
  (:require [compojure.core         :refer [defroutes routes GET context]]
            [compojure.route        :as    route]
            [ring.util.response     :as    resp]
            [ring.middleware.edn    :refer [wrap-edn-params]]
            [ring.middleware.params :refer [wrap-params]]
            [cloc.pages             :as    pages]
            [cloc.index             :as    index]))

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
           (GET "/local" [] (edn-response (index/local-code)))
           (GET "/libs"  [] (edn-response (index/libraries)))
           (GET "/ns" [lib]
                (edn-response (index/namespaces lib)))
           (GET "/docs" [lib namespace]
                (edn-response (index/docs lib namespace)))))

(def main
  (-> (routes pages api)
      (wrap-edn-params)
      (wrap-params)))