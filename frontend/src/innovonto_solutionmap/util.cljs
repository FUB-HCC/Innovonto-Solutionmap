(ns innovonto-solutionmap.util)


(defn fixed-size [idea]
  0.04)


(def random-colors ["#f44336"
                    "#8e24aa"
                    "#303f9f"
                    "#03a9f4"
                    "#009688"
                    "#66bb6a"
                    "#cddc39"
                    "#fdd835"
                    "#ffb300"
                    "#f4511e"
                    "#795548"
                    ])

(defn random-color [idea]
  (rand-nth random-colors))

(defn map-cluster-to-color [idea]
  (nth random-colors (:cluster-label idea)))

