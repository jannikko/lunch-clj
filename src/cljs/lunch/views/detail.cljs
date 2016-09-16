(ns lunch.views.detail
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [reagent.ratom :refer [reaction]]
            [clojure.string :refer [split]]
            ))

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
       [:button {:on-click #(dispatch [:generate-lunch-session])} "Generate Lunch Session"]
       [place-location]
       ])))

(defn detail-panel-did-mount
  []
  (let [params (re-frame/subscribe [:url-params])]
    (dispatch [:initialize-detail-view @params])))


(defn detail-panel []
  (reagent/create-class {:reagent-render      detail-panel-render
                         :component-did-mount detail-panel-did-mount}))
