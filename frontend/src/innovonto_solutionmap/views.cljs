(ns innovonto-solutionmap.views
  (:require [innovonto-solutionmap.events :as events]
            [innovonto-solutionmap.subs :as subs]
            [innovonto-solutionmap.config :as config]
            [re-frame.core :as re-frame]))

;;TODO this hides the tooltip as soon as the mouse leaves the "circle" element. A better behaviour would be: include the tooltip div in the mouse-hover area
;;TODO Desired :on-click behaviour: leaves the tooltip open until the user clicks somewehere else ('pinned')
(defn idea-circle [idea]
  [:circle {:key           (:index idea)
            :cx            (:x idea)
            :cy            (:y idea)
            :r             5
            :class         "idea-circle"
            :on-mouse-over #(re-frame/dispatch [::events/show-tooltip (.-target %1) idea])
            :on-mouse-out  #(re-frame/dispatch [::events/hide-tooltip])}])


;;TOOLTIP STUFF
;[idea-detail-component {:title "Rescue Window" :content "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed."}]
(defn tooltip [config]
  (do
    ;;(println (str "now rerendering tooltip!" config))
    [:div.idea-detail-container {:class (:state config) :style {:top (:top config) :left (:left config)}}
     [:img {:src (:image (:content config))}]
     [:h2 (:title (:content config))]
     [:p (:content (:content config))]]))

(defn debug-panel []
  [:div
   [:button {:on-click #(re-frame/dispatch [::events/debug-print-db])} "Print DB"]
   [:button {:on-click #(re-frame/dispatch [::events/load-data])} "Load Data"]
   [:button {:on-click #(re-frame/dispatch [::events/use-mock-backend])} "Use Mock-Backend"]
   [:button {:on-click #(re-frame/dispatch [::events/use-live-backend])} "Use Live-Backend"]
   [:button {:on-click #(re-frame/dispatch [::events/reset-view-box {:x 0 :y 0 :width 500 :height 500}])} "Reset Viewbox"]
   [:button {:on-click #(re-frame/dispatch [::events/reset-view-box {:x 50 :y 50 :width 500 :height 500}])} "Move Viewbox"]
   ])

(defn foo []
  (let [solutionmap @(re-frame/subscribe [::subs/solutionmap])]
    [:svg {:view-box (:view-box solutionmap)}
     [:rect {:x 0 :y 0 :width 20 :height 20}]
     [:rect {:x 480 :y 0 :width 20 :height 20}]
     [:rect {:x 0 :y 480 :width 20 :height 20}]
     [:rect {:x 480 :y 480 :width 20 :height 20}]]))


#_[:svg.solution-map {:view-box "0 0 500 500"}
   (map idea-circle (:ideas app-state))]

(defn solutionmap-app []
  (let [app-state @(re-frame/subscribe [::subs/app-state])]
    [:div
     (if config/debug?
       [debug-panel])
     [tooltip (:tooltip app-state)]
     [:svg.solution-map {:view-box "0 0 500 500"}
      (map idea-circle (:ideas app-state))]]))


