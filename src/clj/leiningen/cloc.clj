(ns leiningen.cloc
  "Leiningen plugin to start the cloc web server locally."
  (:require [leiningen.core.classpath    :as cp]
            [leiningen.core.eval         :as eval]
            [leiningen.core.project      :as proj]
            [cemerick.pomegranate.aether :as aether]))

(defn dummy-project
  "Create a barebones project with the configured cloc version as
   its only dependency."
  [project]
  (if-let [cloc-vec (first
                     (drop-while
                      (complement
                       (fn [v] (= (first v) 'cloc/cloc)))
                      (:plugins project)))]
    {:dependencies (conj (:dependencies project) cloc-vec)
     :repositories (:repositories project)}
    (throw (Exception. (str "Cloc should be in your :plugins vector, "
                            "either in your ~/.lein/profiles.clj or in "
                            "the project itself.")))))

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
map key and value arguments. Keys should be strings, so omit colons.
Most useful ones will be:

  - host -- host name to listen on             (default: localhost)
  - port -- port to listen for connections on  (default: 1337)"
  [project & args]
  (assert (even? (count args))
            "Number of args should be even - only key-value pairs supported.")
  (let [opts (merge
              {:port 1337}
              (reduce (fn [m [k v ]] (assoc m (keyword k) (try-parse v)))
                      {}
                      (partition 2 args))
              {:join? true})
        jars (mapv str
                  (filter (fn [f] (re-find #"\.jar$" (.getName f)))
                          (aether/dependency-files
                           (cp/dependency-hierarchy :dependencies project))))]
    (eval/eval-in-project (dummy-project project)
                          `(cloc.core/main ~opts ~jars)
                          '(require 'cloc.core))))
