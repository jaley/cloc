(defproject cloc "0.1.0-SNAPSHOT"
  :description "Serve docs from your classpath to a local web server."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.classpath "0.2.0"]
                 [codox/codox.core "0.6.4"]
                 [hiccup "1.0.4"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [fogus/ring-edn "0.2.0"]
                 [org.apache.lucene/lucene-core "4.4.0"]
                 [org.apache.lucene/lucene-analyzers-common "4.4.0"]
                 [org.apache.lucene/lucene-queryparser "4.4.0"]

                 [prismatic/dommy "0.1.1"]
                 [cljs-ajax "0.2.0"]]

  :eval-in-leiningen true

  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-ring "0.8.6"]]

  :source-paths ["src/clj"]

  :cljsbuild {:repl-listen-port 9000
              :repl-launch-commands
              {"firefox" ["firefox" "-jsconsole" "http://localhost:1337/debug"]}

              :builds
              {:dev
                 {:source-paths ["src/cljs"]
                  :jar true
                  :compiler {:output-to "resources/public/js/main-debug.js"
                             :optimizations :whitespace
                             :pretty-print true}}
               :prod
               {:source-paths ["src/cljs"]
                :jar true
                :compiler {:output-to "resources/public/js/main.js"
                           :optimizations :advanced
                           :pretty-print false}}}}

  :ring {:handler cloc.routes/main
         :init    cloc.core/init!
         :port    1337})
