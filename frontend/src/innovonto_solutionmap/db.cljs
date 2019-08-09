(ns innovonto-solutionmap.db)

(def default-db {:text    "Solution Map as SVG + Reagent!"
                 :backend "mock"
                 :ideas   []
                 :view-box {
                            :x 0
                            :y 0
                            :width 1
                            :height 1
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
                                     :description "Lorem Ipsum dolor sit amet, consetetur ..."}}
                 :toolbox {
                           :active-panel :data
                           }})

