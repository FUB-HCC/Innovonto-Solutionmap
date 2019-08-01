(ns innovonto-solutionmap.api
  (:require [re-frame.core :as re-frame]
            [bidi.bidi :as bidi]
            [innovonto-solutionmap.subs :as subs]))

(def mock-api
  ["/mockapi/solutionmap-ideas.json" :solutionmap-ideas])

(def live-api
  ["http://localhost:5000/solutionmap/api/v0.1/get_map?query=PREFIX%20gi2mo%3A%3Chttp%3A%2F%2Fpurl.org%2Fgi2mo%2Fns%23%3E%0A%0ASELECT%20%3Fidea%20%3Fcontent%0AWHERE%20%7B%0A%20%20%3Fidea%20a%20gi2mo%3AIdea.%0A%20%20%3Fidea%20gi2mo%3Acontent%20%3Fcontent.%0A%7D%0AORDER%20BY%20%3Fidea" :solutionmap-ideas])


(defn url-for [endpoint]
  (let [backend @(re-frame/subscribe [::subs/backend])]
    (case backend
      "mock" (bidi/path-for mock-api endpoint)
      "live" (bidi/path-for live-api endpoint))))
