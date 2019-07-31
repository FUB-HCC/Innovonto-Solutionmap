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

(re-frame/reg-sub
  ::solutionmap
  (fn [db _]
    (:solutionmap db)))