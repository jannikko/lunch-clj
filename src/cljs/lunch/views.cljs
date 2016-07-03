(ns lunch.views
    (:require [re-frame.core :as re-frame :refer [dispatch]]))


;; home


(defn home-panel[]
  (let [query (re-frame/subscribe [:query])]
    [:div [:h1 "What would you like to eat today?"]
     [:div [:input {:type "text" :value @query :on-change #(dispatch [:search-input-changed (-> % .-target .-value)])}]
      [:button {:value "Location" :on-click #(dispatch [:location-requested])} "My Location"]]
     ]))


;; detail

(defn detail-panel []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))


;; main

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
      [show-panel @active-panel])))
