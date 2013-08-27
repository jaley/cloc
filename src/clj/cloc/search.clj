(ns cloc.search
  (:import [org.apache.lucene.analysis.standard StandardAnalyzer]
           [org.apache.lucene.document Document Field Field$Store IntField TextField]
           [org.apache.lucene.index DirectoryReader IndexWriter IndexWriterConfig]
           [org.apache.lucene.queryparser.classic QueryParser]
           [org.apache.lucene.search IndexSearcher Query ScoreDoc TopScoreDocCollector]
           [org.apache.lucene.store RAMDirectory]
           [org.apache.lucene.util Version]))

(def ^:private analyzer
  "Standard analyzer, to tokenizer plain text."
  (StandardAnalyzer. Version/LUCENE_40))

(def ^:private directory
  "Holding search index in RAM, as it's fast to rebuild"
  (atom ::unset))

(defn text-field
  "Create a new next field for the Lucene document."
  [name val]
  (TextField. name val Field$Store/YES))

(defn add-var!
  "Add a public var to the search index"
  [writer lib namespace var-info]
  (let [doc (Document.)]
    (.add doc (text-field "name"      (str (:name var-info))))
    (.add doc (text-field "docs"      (str (:doc var-info))))
    (.add doc (text-field "lib"       (str lib)))
    (.add doc (text-field "namespace" (str namespace)))
    (.addDocument writer doc)))

(defn add-publics!
  "Add the given sequence of public vars to the search index."
  [lib namespace publics]
  (let [cfg (IndexWriterConfig. Version/LUCENE_40, analyzer)]
    (with-open [writer (IndexWriter. @directory cfg)]
      (doseq [var-info publics]
        (add-var! writer lib namespace var-info)))))

(defn init-search-index!
  [index]
  (reset! directory (RAMDirectory.))
  (doseq [[lib nses] (concat (:dirs index) (:jars index))]
    (doseq [[namespace info] nses]
      (add-publics! lib namespace (:publics info)))))

(defn search
  "Search for a free-form text string over the search index.
   Returns map sequences with :lib, :namespace, :name and :docs."
  [qry-str]
  (let [query (.parse (QueryParser. Version/LUCENE_40 "docs" analyzer)
                qry-str)]
    (with-open [rdr (DirectoryReader/open @directory)]
      (let [searcher  (IndexSearcher. rdr)
            collector (TopScoreDocCollector/create 10 true)]
        (.search searcher query collector)
        (doall
          (for [hit (. (.topDocs collector) scoreDocs)
                :let [doc (.doc searcher (. hit doc))]]
            {:lib       (.get doc "lib")
             :namespace (.get doc "namespace")
             :name      (.get doc "name")
             :docs      (.get doc "docs")}))))))
