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
  (reagent/create-class {:reagent-render      places-service-node-render
                         :component-did-mount places-service-node-did-mount}))


;; home

(defn home-panel-did-mount
  [this]
  (let [params (re-frame/subscribe [:url-params])]
    (dispatch [:initialize-home-view @params])))

(defn home-panel-render []
  (let [query (re-frame/subscribe [:query])]
    (fn []
      [:div [:h1 "What would you like to eat today?"]
       [:div [:input {:type "text" :value @query :on-change #(dispatch [:search-input-changed (-> % .-target .-value)])}]
        [:button {:value "Location" :on-click #(dispatch [:location-request])} "My Location"]
        [search-results]]
       ])))


(defn home-panel []
  (reagent/create-class {:reagent-render      home-panel-render
                         :component-did-mount home-panel-did-mount
                         }))

;; detail


(defn detail-panel-render []
  (let [menu-link (re-frame/subscribe [:menu-link])
        menu-link-input (re-frame/subscribe [:menu-link-input])]
    (fn []
      [:div (str "This is the Detail Page for: ")
       [:div [:a {:href "#/"} "go to Home Page"]]
       (if-not (some? @menu-link) [:input {:type         "text"
                                           :value        @menu-link-input
                                           :id           "file"
                                           :on-change    #(dispatch [:menu-link-input-changed (-> % .-target .-value)])
                                           :on-key-press #(when (-> % .-charCode (= 13))
                                                           (dispatch [:menu-link-input-submit])
                                                           (-> % .-preventDefault))}]
                                  [:a {:href @menu-link} @menu-link]
                                  )])))

(defn detail-panel-did-mount
  [this]
  (let [params (re-frame/subscribe [:url-params])]
    (dispatch [:initialize-detail-view @params])))


(defn detail-panel []
  (reagent/create-class {:reagent-render      detail-panel-render
                         :component-did-mount detail-panel-did-mount
                         }))

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
      [:div
       [places-service-node]
       [show-panel @active-panel]])))
