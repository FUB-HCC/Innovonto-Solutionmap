(ns innovonto-solutionmap.db)

(def default-db {:text    "Solution Map as SVG + Reagent!"
                 :backend "mock"
                 :ideas   [
                           {:index         0
                            :title         "The 0|0 Idea"
                            :content   "Window with a heat-sensitive material, that lights-up if a person is in the room during a fire"
                            :thumbnailPath "img/icona-crop-u12770.jpg"
                            :x             0
                            :y             0}
                           {:index         1
                            :title         "The 1200|600 Idea"
                            :content   "foo to the bar!"
                            :thumbnailPath "img/11.png"
                            :x             1200
                            :y             600}
                           {:index         2
                            :title         "Rescue Window"
                            :content   "this is a description!"
                            :thumbnailPath "img/16.png"
                            :x             100.8
                            :y             100.202352345}]
                 :solutionmap {
                               :view-box "0 0 500 500"
                               }
                 :tooltip {
                           :state   "hidden"
                           :top     "0px"
                           :left    "0px"
                           :cx      0
                           :cy      0
                           :content {
                                     :image       "img/icona-crop-u12770.jpg"
                                     :title       "Rescue Window"
                                     :description "Lorem Ipsum dolor sit amet, consetetur ..."}}})

