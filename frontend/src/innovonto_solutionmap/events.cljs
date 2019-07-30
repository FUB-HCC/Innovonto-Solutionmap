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

(defn build-scale [values]
  (do
    (geom/linear-scale [(apply min values) (apply max values)]
                       [100 400])))

(defn re-scale [x-scale y-scale idea]
  (-> idea
      (assoc :x (x-scale (:x idea)))
      (assoc :y (y-scale (:y idea)))))

(defn transform-all-ideas [ideas]
  (let [x-scale (build-scale (map :x ideas))
        y-scale (build-scale (map :y ideas))]
    (map (partial re-scale x-scale y-scale) ideas)))

(re-frame/reg-event-db
  ::init-db-from-server-data
  (fn [db [_ response]]
    (do
      ;;(println (str "init! response:" response))
      (assoc db :ideas (transform-all-ideas (:ideas response))))))

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
     :left    (str (+ (.-pageXOffset js/window) (.-e matrix) -130) "px")
     :top     (str (+ (.-pageYOffset js/window) (.-f matrix) 10) "px")
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


(defn toViewboxString [{:keys [x y width height]}]
  (str x " " y " " width " " height))

;; Solution-Map Handler
(re-frame/reg-event-db
  ::reset-view-box
  (fn [db [_ new-viewbox]]
    (update-in db [:solutionmap] assoc :view-box (toViewboxString new-viewbox))))