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
                                   :pointer-origin  nil
                                   }))

(defn create-js-point [event]
  (let [svg (.getElementById js/document "solution-map")
        point (.createSVGPoint svg)
        inverted-svg-matrix (.inverse (.getScreenCTM svg))]
    (set! (.-x point) (.-clientX event))
    (set! (.-y point) (.-clientY event))
    (.matrixTransform point inverted-svg-matrix)))

(defn handle-pointer-down [event]
  (let [point (create-js-point event)]
    (reset! svg-navigation
            (-> @svg-navigation
                (assoc :is-pointer-down true)
                (assoc :pointer-origin point)
                ))))

(defn handle-pointer-up [event]
  (swap! svg-navigation #(assoc %1 :is-pointer-down false)))

(defn handle-pointer-leave [event]
  true)

;;TODO the amount the origin is moved depents on ???
(defn handle-pointer-move [event]
  (if (:is-pointer-down @svg-navigation)
    (let [view-box @(re-frame/subscribe [::subs/view-box])
          pointer-position (create-js-point event)
          pointer-origin (:pointer-origin @svg-navigation)
          new-origin {
                      :x (- (:x view-box) (- (.-x pointer-position) (.-x pointer-origin)))
                      :y (- (:y view-box) (- (.-y pointer-position) (.-y pointer-origin)))
                      }]
      (.preventDefault event)
      ;;(println (str "moving pointer from: " pointer-origin " to " pointer-position))
      ;;(println (str "new origin is: " new-origin))
      (re-frame/dispatch [::events/reset-view-box-origin new-origin]))))

(defn solution-map-svg-component []
  (let [viewbox @(re-frame/subscribe [::subs/view-box-string])
        ideas @(re-frame/subscribe [::subs/ideas])]
    [:div.solution-map-container
     [:svg#solution-map.solution-map {
                                      :view-box       viewbox
                                      :on-wheel       handle-mousewheel
                                      :on-mouse-down  handle-pointer-down
                                      :on-mouse-up    handle-pointer-up
                                      :on-mouse-leave handle-pointer-leave
                                      :on-mouse-move  handle-pointer-move
                                      }
      (map idea-circle ideas)]]))

(defn view-box-navigator []
  [:div.solution-map-navigator
   [:button {:on-click #(re-frame/dispatch [::events/zoom-in])} "+"]
   [:button {:on-click #(re-frame/dispatch [::events/zoom-out])} "-"]
   [:div.nav-controller
    [:div.row
     [:p.up {:on-click #(re-frame/dispatch [::events/pan-up])} "↑"]]
    [:div.row
     [:p.left {:on-click #(re-frame/dispatch [::events/pan-left])} "←"]
     [:p.center {:on-click #(re-frame/dispatch [::events/reset-view-box])} "O"]
     [:p.right {:on-click #(re-frame/dispatch [::events/pan-right])} "→"]
     ]
    [:div.row
     [:p.down {:on-click #(re-frame/dispatch [::events/pan-down])} "↓"]]]
   ])

(defn map-config-panel []
  [:span "TODO"])

(defn option-component [option]
  [:option {:key   option
            :value option} (str option)])

(defn select-component [current-option available-options dispatch-event]
  [:select {:default-value current-option
            :on-change     #(re-frame/dispatch [dispatch-event (.-value (.-target %1))])}
   (map option-component available-options)])

(defn data-config-panel []
  [:div
   [:div.config-row
    [:span "Endpoint:"]
    [select-component @(re-frame/subscribe [::subs/backend]) ["mock" "live"] ::events/switch-backend]]

   [:div.config-row
    [:span "Challenge:"]
    [:select
     [:option {:value "TCO"} "TCO"]]]
   [:div.config-row
    [:p "Algorithm:"]
    [:div
     [:p "Embedding:"]
     [select-component "USE" ["USE" "RANDOM"] ::events/set-embedding-algorithm]
     [:p "Reduction: "]
     [select-component "t-sne" ["t-sne" "PCA"] ::events/set-reduction-algorithm]
     [:p "Clustering: "]
     [select-component "k-means" ["k-means" "TODO-1" "TODO-2"] ::events/set-clustering-algorithm]]]
   [:hr]
   [:div.config-row
    [:button {:on-click #(re-frame/dispatch [::events/load-data])} "Reload!"]]])

(defn is-active? [current-active panel]
  (if (= current-active panel)
    "active"
    ""))

(defn toolbox []
  (let [toolbox-panel (re-frame/subscribe [::subs/active-toolbox-panel])]
    [:div.toolbox
     [:div.toolbox-header
      [:div.toolbox-header-item {:class (is-active? @toolbox-panel :data) :on-click #(re-frame/dispatch [::events/switch-toolbox-panel :data])} [:span "Data"]]
      [:div.toolbox-header-item {:class (is-active? @toolbox-panel :nav) :on-click #(re-frame/dispatch [::events/switch-toolbox-panel :nav])} [:span "Nav"]]
      [:div.toolbox-header-item {:class (is-active? @toolbox-panel :map-config) :on-click #(re-frame/dispatch [::events/switch-toolbox-panel :map-config])} [:span "Map-Config"]]]
     [:div.toolbox-body
      (case @toolbox-panel
        :data [data-config-panel]
        :nav [view-box-navigator]
        :map-config [map-config-panel]
        [:span "Unrecognized active toolbox panel"])]]))

(defn header []
  [:header.navbarHeader
   [:div.logo [:h1 "Innovonto - Solution Map"]]
   (if config/debug?
     [debug-panel])
   ])

(defn solutionmap-app []
  (let [app-state @(re-frame/subscribe [::subs/app-state])]
    [:div
     [header]
     [:div.container
      [tooltip (:tooltip app-state)]
      [:div.row
       [solution-map-svg-component]
       [toolbox]]]]))