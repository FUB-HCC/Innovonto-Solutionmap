(ns innovonto-solutionmap.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::app-state
  (fn [db _]
    db))


(re-frame/reg-sub
  ::backend
  (fn [db _]
    (:backend db)))

(defn to-view-box-string [{:keys [x y width height]}]
  (str x " " y " " width " " height))

(re-frame/reg-sub
  ::view-box-string
  (fn [db _]
    (to-view-box-string (:view-box db))))

(re-frame/reg-sub
  ::view-box
  (fn [db _]
    (:view-box db)))

(re-frame/reg-sub
  ::ideas
  (fn [db _]
    (:ideas db)))

(re-frame/reg-sub
  ::active-toolbox-panel
  (fn [db _]
    (get-in db [:toolbox :active-panel])))