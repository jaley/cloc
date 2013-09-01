(ns leiningen.cloc
  "Leiningen plugin to start the cloc web server locally."
  (:require [leiningen.core.classpath    :as cp]
            [leiningen.core.eval         :as eval]
            [cemerick.pomegranate.aether :as aether]))

(defn- try-parse
  "Try to convert v to integer, then bool, else string."
  [v]
  (if-let [num (try (Integer/parseInt v) (catch NumberFormatException _ nil))]
    num
    (cond
     (re-find #"true|flase" (.toLowerCase v)) (Boolean/parseBoolean v)
     :else (str v))))

(defn- project-with-indexer
  "Add cloc indexer to project dependencies."
  [project]
  (-> project
      (assoc :eval-in-leiningen false)
      (update-in [:plugins] (fn [plugins]
                              (vec (remove (fn [[dep _]]
                                             (= dep 'cloc/cloc))
                                           plugins))))
      (update-in [:dependencies] conj ['cloc/indexer "0.1.0-SNAPSHOT"])))

(defn- doc-edn-path
  "Path to dump temporary file with generated edn documentation."
  [project]
  (.getAbsolutePath
   (java.io.File. (:target-path project) "cloc.edn")))

(defn- project-docs!
  "Run codox in the project to get the documentation map."
  [project paths]
  (let [doc-path (doc-edn-path project)]
    (eval/eval-in-project (project-with-indexer project)
                          `(do
                             (spit ~doc-path
                                   (cloc.indexer/index-classpath ~paths))
                             (System/exit 0))
                          '(require 'cloc.indexer))))

(defn- cloc-project
  "Create a project to launch cloc, with only cloc as a dependency."
  [project]
  (if-let [cloc-vec (first
                     (drop-while
                      (complement
                       (fn [v] (= (first v) 'cloc/cloc)))
                      (:plugins project)))]
    {:eval-in-leingen false
     :dependencies [cloc-vec]}
    (throw (Exception. (str "Cloc should be in your :plugins vector, "
                            "either in your ~/.lein/profiles.clj or in "
                            "the project itself.")))))

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
  (let [opts  (merge
               {:port 1337}
               (reduce (fn [m [k v ]] (assoc m (keyword k) (try-parse v)))
                       {}
                       (partition 2 args))
               {:join? true})
        jars  (mapv str
                    (filter (fn [f] (re-find #"\.jar$" (.getName f)))
                            (aether/dependency-files
                             (cp/dependency-hierarchy :dependencies project))))
        paths (reduce conj jars (concat (:source-paths project)
                                        (:test-paths   project)))]
    (project-docs! project paths)
    (eval/eval-in-project (cloc-project project)
                          `(cloc.core/main '~opts ~(doc-edn-path project))
                          '(require 'cloc.core))))
