(ns lunch.views
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [lunch.routes :refer [detail-route]]
            [lunch.views.home :refer [home-panel]]
            [lunch.views.detail :refer [detail-panel]]
            [lunch.views.places-service :refer [places-service]]))

;; TODO serve html from server to embed configuration such as api keys

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :detail-panel [] [detail-panel])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div
       [places-service]
       [show-panel @active-panel]])))
