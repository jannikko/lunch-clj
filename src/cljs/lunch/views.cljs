(ns lunch.views
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [lunch.routes :refer [detail-route]]
            ))

(defn search-result-entry
  [{:keys [place_id name formatted_address]}]
  [:div
   [:a {:href (detail-route {:id place_id})} name " " formatted_address]])

(defn places-service-node-render
  "The places api needs a node to render to even when there is no map on the website"
  []
  [:div])

(defn search-results []
  (let [result (re-frame/subscribe [:search-result])]
    (fn []
      (into [:div] (map search-result-entry @result)))))

(defn places-service-node-did-mount
  "Instantiate the places service from the google api"
  [this]
  (dispatch [:initialize-places-service (reagent/dom-node this)]))

(defn places-service-node []
  (reagent/create-class {:reagent-render places-service-node-render
                         :component-did-mount places-service-node-did-mount}))


;; home

(defn home-panel[]
  (let [query (re-frame/subscribe [:query])]
    (fn []
      [:div [:h1 "What would you like to eat today?"]
       [:div [:input {:type "text" :value @query :on-change #(dispatch [:search-input-changed (-> % .-target .-value)])}]
        [:button {:value "Location" :on-click #(dispatch [:location-request])} "My Location"]
        [places-service-node]
        [search-results]]
       ])))


;; detail

(defn detail-panel []
  (let [params (re-frame/subscribe [:url-params])]
      (fn []
        (println params)
        [:div (str "This is the Detail Page for: " (:id @params))
         [:div [:a {:href "#/"} "go to Home Page"]]])))


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
