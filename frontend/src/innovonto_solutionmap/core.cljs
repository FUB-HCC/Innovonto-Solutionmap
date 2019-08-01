(ns ^:figwheel-hooks innovonto-solutionmap.core
  (:require
    [goog.dom :as gdom]
    [re-frame.core :as re-frame]
    [innovonto-solutionmap.views :as views]
    [innovonto-solutionmap.db :as db]
    [reagent.core :as reagent]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  ::force-rerender
  (fn [db _]
    (assoc db :__figwheel_counter (inc (:__figwheel_counter db)))))

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (re-frame/clear-subscription-cache!)
  (re-frame/dispatch-sync [::initialize-db])
  (reagent/render-component [views/solutionmap-app] el))


(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
;;TODO move this into a "run" method!
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  ;;(mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  (re-frame/dispatch-sync [::force-rerender]))