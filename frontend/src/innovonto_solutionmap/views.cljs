(ns innovonto-solutionmap.views
  (:import [goog.async Debouncer])
  (:require [innovonto-solutionmap.events :as events]
            [innovonto-solutionmap.subs :as subs]
            [innovonto-solutionmap.config :as config]
            [innovonto-solutionmap.util :as util]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]))


(def display-config {
                     :size-fn  util/fixed-size
                     :color-fn util/map-cluster-to-color
                     })

;;TODO this hides the tooltip as soon as the mouse leaves the "circle" element. A better behaviour would be: include the tooltip div in the mouse-hover area
;;TODO Desired :on-click behaviour: leaves the tooltip open until the user clicks somewehere else ('pinned')
(defn idea-circle [idea]
  [:circle {:key           (:id idea)
            :cx            (:x idea)
            :cy            (:y idea)
            :r             ((get display-config :size-fn) idea)
            :class         "idea-circle"
            :style         {:fill ((get display-config :color-fn) idea)}
            :on-mouse-over #(re-frame/dispatch [::events/show-tooltip (.-target %1) idea])
            :on-click      #(re-frame/dispatch [::events/show-tooltip (.-target %1) idea])
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

(defn handle-mousewheel [event]
  (let [direction (Math/sign (.-deltaY event))]
    (case direction
      1 (re-frame/dispatch [::events/zoom-out])
      -1 (re-frame/dispatch [::events/zoom-in])
      "unknown direction!")))


(def svg-navigation (reagent/atom {
                                   :is-pointer-down false
                                   :pointer-origin  {:x 0 :y 0}
                                   }))

(defn get-point-from-event [event]
  {
   :x (.-clientX event)
   :y (.-clientY event)
   })

(defn handle-pointer-down [event]
  (do
    ;;(println (str "New pointer origin is: " (get-point-from-event event)))
    (reset! svg-navigation
            (-> @svg-navigation
                (assoc :pointer-origin (get-point-from-event event))
                (assoc :is-pointer-down true)))))

(defn handle-pointer-up [event]
  (swap! svg-navigation #(assoc %1 :is-pointer-down false)))

(defn handle-pointer-leave [event]
  true)

;;TODO the amount the origin is moved depents on ???
(defn handle-pointer-move [event]
  (if (:is-pointer-down @svg-navigation)
    (let [view-box @(re-frame/subscribe [::subs/view-box])
          pointer-position (get-point-from-event event)
          pointer-origin (:pointer-origin @svg-navigation)
          new-origin {
                      :x (- (:x view-box) (* (- (:x pointer-position) (:x pointer-origin)) 0.03))
                      :y (- (:y view-box) (* (- (:y pointer-position) (:y pointer-origin)) 0.03))
                      }]
      (.preventDefault event)
      ;;(println (str "moving pointer from: " pointer-origin " to " pointer-position))
      ;;(println (str "new origin is: " new-origin))
      (re-frame/dispatch [::events/reset-view-box-origin new-origin]))))

(defn solution-map-svg-component []
  (let [viewbox @(re-frame/subscribe [::subs/view-box-string])
        ideas @(re-frame/subscribe [::subs/ideas])]
    [:svg.solution-map {
                        :view-box       viewbox
                        :on-wheel       handle-mousewheel
                        :on-mouse-down  handle-pointer-down
                        :on-mouse-up    handle-pointer-up
                        :on-mouse-leave handle-pointer-leave
                        :on-mouse-move  handle-pointer-move
                        }
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

(defn header []
  [:header.navbarHeader
   [:div.logo [:h1 "Innovonto"]]
   (if config/debug?
     [debug-panel])
   ])

(defn solutionmap-app []
  (let [app-state @(re-frame/subscribe [::subs/app-state])]
    [:div
     [header]
     [:div.container
      [:h1 "Solution Map"]
      [tooltip (:tooltip app-state)]
      [:div.solution-map-container
       [solution-map-svg-component]
       [view-box-navigator]]]]))