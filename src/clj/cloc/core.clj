(ns cloc.core
  "Cloc main entry point, for Jetty web server."
  (:require [clojure.java.classpath :as cp]
            [ring.adapter.jetty     :as jetty]
            [cloc.routes            :as routes]
            [cloc.index             :as index]
            [cloc.search            :as search]))

(def server (atom nil))

(defn start-server!
  "Start a jetty server"
  [& [ring-opts]]
  (swap! server
         (fn [s]
           (when s (.stop s))
           (jetty/run-jetty routes/main
                            (or ring-opts
                                {:port 1337, :join? false})))))

(defn stop-server!
  "Stop the jetty server, if one is running."
  []
  (swap! server (fn [s] (when s (.stop s)))))

(defn init!
  "Ring initialiser function.
   When working interactively, call this before start-server!"
  [& [classpath]]
  (index/init-index! (or classpath (cp/classpath)))
  (search/init-search-index! @index/index))

(defn main
  [ring-opts classpath]
  (init! classpath)
  (start-server! ring-opts))
