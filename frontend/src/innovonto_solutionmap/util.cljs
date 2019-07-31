(ns innovonto-solutionmap.util)


(defn fixed-size [idea]
  0.009)


(def random-colors ["#A4DE02"
                    "#76BA1B"
                    "#4C9A2A"
                    "#ACDF87"
                    "#68BB59"])

(defn random-color [idea]
  (rand-nth random-colors))