(ns cloc.core
  "Cloc main entry point, for Jetty web server."
  (:require [ring.adapter.jetty :as jetty]
            [cloc.routes        :as routes]
            [cloc.index         :as index]
            [cloc.search        :as search]))

(def server (atom nil))

(defn start-server!
  "Start a jetty server"
  []
  (swap! server
         (fn [s]
           (when s (.stop s))
           (jetty/run-jetty routes/main
                            {:port 1337, :join? false}))))

(defn stop-server!
  "Stop the jetty server, if one is running."
  []
  (swap! server (fn [s] (when s (.stop s)))))

(defn init!
  "Ring initialiser function.
   When working interactively, call this before start-server!"
  [& _]
  (index/init-index!)
  (search/init-search-index! @index/index))
