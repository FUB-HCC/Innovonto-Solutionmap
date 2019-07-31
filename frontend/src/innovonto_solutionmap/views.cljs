(ns innovonto-solutionmap.views
  (:require [innovonto-solutionmap.events :as events]
            [innovonto-solutionmap.subs :as subs]
            [innovonto-solutionmap.config :as config]
            [re-frame.core :as re-frame]))

;;TODO this hides the tooltip as soon as the mouse leaves the "circle" element. A better behaviour would be: include the tooltip div in the mouse-hover area
;;TODO Desired :on-click behaviour: leaves the tooltip open until the user clicks somewehere else ('pinned')
(defn idea-circle [idea]
  [:circle {:key           (:id idea)
            :cx            (:x idea)
            :cy            (:y idea)
            :r             0.005
            :class         "idea-circle"
            :on-mouse-over #(re-frame/dispatch [::events/show-tooltip (.-target %1) idea])
            :on-mouse-out  #(re-frame/dispatch [::events/hide-tooltip])}])


(defn idea-card [idea]
  [:div
   [:p (:content idea)]])

;;TOOLTIP STUFF
(defn tooltip [config]
  (do
    ;;(println (str "now rerendering tooltip!" config))
    [:div.idea-detail-container {:class (:state config) :style {:top (:top config) :left (:left config)}}
     [idea-card (:content config)]]))

(defn debug-panel []
  [:div
   [:button {:on-click #(re-frame/dispatch [::events/debug-print-db])} "Print DB"]
   [:button {:on-click #(re-frame/dispatch [::events/load-data])} "Load Data"]
   [:button {:on-click #(re-frame/dispatch [::events/use-mock-backend])} "Use Mock-Backend"]
   [:button {:on-click #(re-frame/dispatch [::events/use-live-backend])} "Use Live-Backend"]
   ])



(defn view-box-control [keyword view-box]
  [:div
   [:label (str (name keyword) ": ")]
   [:input {:type          "text"
            :name          (name keyword)
            :default-value (keyword view-box)
            :on-blur       #(re-frame/dispatch [::events/update-viewbox-element keyword (-> % .-target .-value)])}]])


(defn view-box-tools []
  (let [view-box @(re-frame/subscribe [::subs/view-box])]
    [:div
     [view-box-control :x view-box]
     [view-box-control :y view-box]
     [view-box-control :width view-box]
     [view-box-control :height view-box]]))

(defn solution-map-svg-component []
  (let [viewbox @(re-frame/subscribe [::subs/view-box-string])
        ideas @(re-frame/subscribe [::subs/ideas])]
    [:svg.solution-map {:view-box viewbox}
     (map idea-circle ideas)]))

(defn view-box-navigator []
  [:div.solution-map-navigator
   [:button {:on-click #(re-frame/dispatch [::events/zoom-in])} "+"]
   [:button {:on-click #(re-frame/dispatch [::events/zoom-out])} "-"]
   [:div.nav-controller
    [:div.row
     [:p.up {:on-click #(re-frame/dispatch [::events/pan-up])} "\u2B9D"]]
    [:div.row
     [:p.left {:on-click #(re-frame/dispatch [::events/pan-left])} "\u2B9C"]
     [:p.center {:on-click #(re-frame/dispatch [::events/reset-view-box])} "⭘"]
     [:p.right {:on-click #(re-frame/dispatch [::events/pan-right])} "\u2B9E"]
     ]
    [:div.row
     [:p.down {:on-click #(re-frame/dispatch [::events/pan-down])} "\u2B9F"]]]
   ])

(defn solutionmap-app []
  (let [app-state @(re-frame/subscribe [::subs/app-state])]
    [:div
     (if config/debug?
       [debug-panel])
     [tooltip (:tooltip app-state)]
     [:div.solution-map-container
      [solution-map-svg-component]
      [view-box-navigator]]]))