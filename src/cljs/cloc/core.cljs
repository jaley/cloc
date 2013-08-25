(ns cloc.core
  (:require [cloc.searchbar :as searchbar]
            [cloc.browser   :as browser]
            [cloc.docpage   :as docpage]))

(defn ^:export load-ui
  "Load the cloc UI components."
  []
  (searchbar/init!)
  (browser/init!)
  (docpage/show-landing!))
