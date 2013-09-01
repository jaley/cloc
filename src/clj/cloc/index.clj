(ns cloc.index
  "Functions for access the documentation index structure."
  (:require [cloc.indexer :as indexer]))

(def index
  "The index is cached in this atom. Call init-index! before
   using the other function."
  (atom ::unset))

(defn init-index-from-classpath!
  "Use cloc/indexer to generate an index from classpath."
  [classpath]
  (reset! index (indexer/index-classpath classpath)))

(defn init-index-from-lein!
  "Initialise index state to given pre-generated index."
  [idx]
  (reset! index idx))
