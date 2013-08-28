(ns leiningen.cloc
  "Leiningen plugin to start the cloc web server locally."
  (:require [leiningen.core.classpath    :as cp]
            [leiningen.core.eval         :as eval]
            [cemerick.pomegranate.aether :as aether]))

(def dummy-project
  {:dependencies [['cloc "0.1.0-SNAPSHOT"]]})

(defn- try-parse
  "Try to convert v to integer, then bool, else string."
  [v]
  (if-let [num (try (Integer/parseInt v) (catch NumberFormatException _ nil))]
    num
    (cond
     (re-find #"true|flase" (.toLowerCase v)) (Boolean/parseBoolean v)
     :else (str v))))

(defn cloc
  "Start the cloc doc server locally, serving API docs for
   your code and all your dependencies. Accepts ring configuration
   map keyword and value arguments. Most useful ones will be:

    - :host -- host name to listen on
    - :port -- port to listen for connections on"
  [project & args]
  (let [opts (merge
              (reduce (fn [m [k v ]] (assoc m (keyword k) (try-parse v)))
                      {}
                      (partition 2 args))
              {:port 1337 :join? true})
        jars (mapv str
                  (filter (fn [f] (re-find #"\.jar$" (.getName f)))
                          (aether/dependency-files
                           (cp/dependency-hierarchy :dependencies project))))]
    (assert (even? (count args))
            "Number of args should be even - only key-value pairs supported.")
    (eval/eval-in-project dummy-project
                          `(cloc.core/main ~opts ~jars)
                          '(require 'cloc.core))))
