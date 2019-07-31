(ns innovonto-solutionmap.api
  (:require [re-frame.core :as re-frame]
            [bidi.bidi :as bidi]
            [innovonto-solutionmap.subs :as subs]))

(def mock-api
  ["/mockapi/solutionmap-ideas.json" :solutionmap-ideas])

(def live-api
  ["/api/solutionmap-ideas" :solutionmap-ideas])


(defn url-for [endpoint]
  (let [backend @(re-frame/subscribe [::subs/backend])]
    (case backend
      "mock" (bidi/path-for mock-api endpoint)
      "live" (bidi/path-for live-api endpoint))))
