(ns cloc.core
  (:require [cloc.browser :as browser]
            [cloc.docpage :as docpage]))

(defn ^:export load-ui
  "Load the cloc UI components."
  []
  (browser/init!)
  (docpage/show-landing!))
