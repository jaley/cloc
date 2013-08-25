(ns cloc.core
  "Cloc main entry point, for Jetty web server."
  (:require [ring.adapter.jetty :as jetty]
            [cloc.routes        :as routes]
            [cloc.index         :as index]))

(def server (atom nil))

(defn start-server!
  []
  (index/init-index!)
  (swap! server
         (fn [s]
           (when s (.stop s))
           (jetty/run-jetty routes/main
                            {:port 1337, :join? false}))))

(defn stop-server!
  []
  (swap! server (fn [s] (when s (.stop s)))))
