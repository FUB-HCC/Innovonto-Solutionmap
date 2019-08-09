(ns innovonto-solutionmap.events
  (:require [re-frame.core :as re-frame]
            [thi.ng.geom.viz.core :as geom]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [innovonto-solutionmap.api :as api]))


(re-frame/reg-event-db
  ::debug-print-db
  (fn [db _]
    (do
      (println db)
      db)))

(defn to-idea [binding]
  {
   :id            (get-in binding [:idea :value])
   :content       (get-in binding [:content :value])
   :cluster-label (js/parseInt (get-in binding [:cluster_label]))
   :x             (js/parseFloat (get-in binding [:coordinates :x]))
   :y             (js/parseFloat (get-in binding [:coordinates :y]))})

(defn sparql-response-to-ideas [response]
  (map to-idea (get-in response [:results :bindings])))


(def default-bounding-box {:min-x 0 :min-y 0 :max-x 0 :max-y 0})

(defn update-bounding-box [idea bounding-box]
  {
   :min-x (min (:x idea) (:min-x bounding-box))
   :min-y (min (:y idea) (:min-y bounding-box))
   :max-x (max (:x idea) (:max-x bounding-box))
   :max-y (max (:y idea) (:max-y bounding-box))
   })

(defn calculate-bounding-box [bounding-box ideas]
  (if (empty? ideas)
    bounding-box
    (let [head (first ideas)
          tail (rest ideas)]
      (calculate-bounding-box (update-bounding-box head bounding-box) tail))))

(defn to-view-box [bounding-box]
  {
   :x      (:min-x bounding-box)
   :y      (:min-y bounding-box)
   :width  (- (:max-x bounding-box) (:min-x bounding-box))
   :height (- (:max-y bounding-box) (:min-y bounding-box))})

(re-frame/reg-event-db
  ::init-db-from-server-data
  (fn [db [_ response]]
    (let [ideas (sparql-response-to-ideas response)
          bounding-box (calculate-bounding-box default-bounding-box ideas)]
      (println (str "Calculated Bounding Box: " bounding-box))
      (-> db
          (assoc :ideas ideas)
          (assoc :view-box (to-view-box bounding-box))
          (assoc :bounding-box bounding-box)))))

(re-frame/reg-event-db
  ::init-error
  (fn [db [_ error]]
    (do
      (println "Init returned an error: " error)
      (assoc db :state "error"))))

(defn update-tooltip [target idea]
  (let [matrix (.translate (.getScreenCTM target) (.getAttribute target "cx") (.getAttribute target "cy"))]
    {
     :state   "shown"
     :left    (str (+ (.-pageXOffset js/window) (.-e matrix)) "px")
     :top     (str (+ (.-pageYOffset js/window) (.-f matrix) 30) "px")
     :content {:image (:thumbnailPath idea) :title (:title idea) :content (:content idea)}}))

(re-frame/reg-event-db
  ::show-tooltip
  (fn [db [_ target idea]]
    (do
      ;;(println (str "Now Showing:" idea " in tooltip"))
      (assoc db :tooltip (update-tooltip target idea))
      )))

(re-frame/reg-event-db
  ::hide-tooltip
  (fn [db _]
    (update-in db [:tooltip] assoc :state "hidden")))

(re-frame/reg-event-db
  ::use-mock-backend
  (fn [db _]
    (assoc db :backend "mock")))

(re-frame/reg-event-db
  ::use-live-backend
  (fn [db _]
    (assoc db :backend "live")))

(re-frame/reg-event-fx
  ::load-data
  (fn [{:keys [db]} _]
    {
     :db         (assoc db :state "loading")
     :http-xhrio {
                  :method          :get
                  :uri             (api/url-for :solutionmap-ideas)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::init-db-from-server-data]
                  :on-failure      [::init-error]}}))

;; Solution-Map Handler
(re-frame/reg-event-db
  ::reset-view-box
  (fn [db _]
    (assoc db :view-box (to-view-box (:bounding-box db)))))

(re-frame/reg-event-db
  ::update-viewbox-element
  (fn [db [_ keyword new-value]]
    (do
      ;;(println (str "New Value for " keyword " is " new-value))
      (update-in db [:view-box] assoc keyword new-value))))

(re-frame/reg-event-db
  ::zoom-in
  (fn [db _]
    (let [current-view-box (:view-box db)]
      (assoc db :view-box {
                           :x      (+ (:x current-view-box) (* (:width current-view-box) 0.1))
                           :y      (+ (:y current-view-box) (* (:height current-view-box) 0.1))
                           :width  (* (:width current-view-box) 0.8)
                           :height (* (:height current-view-box) 0.8)}))))

(re-frame/reg-event-db
  ::zoom-out
  (fn [db _]
    (let [current-view-box (:view-box db)]
      (assoc db :view-box {
                           :x      (- (:x current-view-box) (* (:width current-view-box) 0.1))
                           :y      (- (:y current-view-box) (* (:height current-view-box) 0.1))
                           :width  (* (:width current-view-box) 1.2)
                           :height (* (:height current-view-box) 1.2)}))))

(re-frame/reg-event-db
  ::pan-up
  (fn [db _]
    (let [current-view-box (:view-box db)]
      (assoc-in db [:view-box :y] (- (:y current-view-box) (* (:height current-view-box) 0.2))))))

(re-frame/reg-event-db
  ::pan-down
  (fn [db _]
    (let [current-view-box (:view-box db)]
      (assoc-in db [:view-box :y] (+ (:y current-view-box) (* (:height current-view-box) 0.2))))))

(re-frame/reg-event-db
  ::pan-left
  (fn [db _]
    (let [current-view-box (:view-box db)]
      (assoc-in db [:view-box :x] (- (:x current-view-box) (* (:width current-view-box) 0.2))))))

(re-frame/reg-event-db
  ::pan-right
  (fn [db _]
    (let [current-view-box (:view-box db)]
      (assoc-in db [:view-box :x] (+ (:x current-view-box) (* (:width current-view-box) 0.2))))))

(re-frame/reg-event-db
  ::reset-view-box-origin
  (fn [db [_ new-origin]]
    (-> db
        (assoc-in [:view-box :x] (:x new-origin))
        (assoc-in [:view-box :y] (:y new-origin)))))

;;TOOLBOX
(re-frame/reg-event-db
  ::switch-toolbox-panel
  (fn [db [_ panel]]
    (assoc-in db [:toolbox :active-panel] panel)))