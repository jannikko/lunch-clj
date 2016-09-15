(ns lunch.views
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [lunch.routes :refer [detail-route]]
            [reagent.ratom :refer [reaction]]
            [clojure.string :refer [split]]
            ))

;; TODO serve html from server to embed configuration such as api keys
;; TODO move logic that belongs together in separate files

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

(defn render-address
  [address]
  (map (fn [addr] [:span addr]) (split address #", ")))

(defn place-address
  []
  (let [detail-result (re-frame/subscribe [:detail-result])
        name (reaction (:name @detail-result))
        address (reaction (:formatted_address @detail-result))
        phone-number (reaction (:international_phone_number @detail-result))
        website (reaction (:website @detail-result))]
  (fn []
    [:div [:h1 @name]
     [:div (render-address @address) [:span @phone-number] [:a {:href @website} @website]]])))

(defn place-location
  []
  (let [place-id (re-frame/subscribe [:place-id])]
  (fn []
     [:div [:iframe {:width       600
                     :height      450
                     :frameborder 0
                     :src         (str "https://www.google.com/maps/embed/v1/place?key=AIzaSyCDbcLwR97CQ5xrMxYGWo5H1kL4tN3VJjQ&q=place_id:" @place-id)}]])))

(defn detail-panel-render []
  (let [menu-link (re-frame/subscribe [:menu-link])
        menu-link-input (re-frame/subscribe [:menu-link-input])]
    (fn []
      [:div [place-address]
       [:div [:a {:href "#/"} "go to Home Page"]]
       [:div [:label {:for "menu-link"} "Menu Link: "]
        (if-not (some? @menu-link) [:input {:type         "text"
                                            :value        @menu-link-input
                                            :id           "menu-link"
                                            :on-change    #(dispatch [:menu-link-input-changed (-> % .-target .-value)])
                                            :on-key-press #(when (-> % .-charCode (= 13))
                                                            (dispatch [:menu-link-input-submit])
                                                            (-> % .-preventDefault))}]
                                   [:a {:href @menu-link :id "menu-link"} @menu-link])]
       [place-location]
       ])))

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
