(ns lunch.views.components.place
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.ratom :refer [reaction]]
            [reagent.core :as reagent]
            [clojure.string :refer [split]]))

(defn- place-contact
  []
  (let [place-contact (re-frame/subscribe [:place-contact])
        menu-link (re-frame/subscribe [:menu-link])
        menu-link-input (re-frame/subscribe [:menu-link-input])
        name (reaction (:name @place-contact))
        phone-number (reaction (:international_phone_number @place-contact))
        website (reaction (:website @place-contact))]
    (fn []
      [:div {:class "card"}
       [:div {:class "card-block"}
        [:h4 {:class "card-title"} @name]
        [:a {:class "card-link" :href @website} @website] [:a {:class "card-link" :href (str "tel:" @phone-number)} @phone-number]]
       [:div {:class "card-block"}
        [:h6 {:class "card-title"} "Menu"]
        (if-not (some? @menu-link)
          [:input {:type         "text"
                   :value        @menu-link-input
                   :id           "menu-link"
                   :class        "form-control"
                   :on-change    #(dispatch [:menu-link-input-changed (-> % .-target .-value)])
                   :on-key-press #(when (-> % .-charCode (= 13))
                                   (dispatch [:menu-link-input-submit])
                                   (-> % .-preventDefault))}]
          [:a {:class "card-link" :href @menu-link :id "menu-link"} @menu-link])]])))

(defn place-map
  []
  (let [place-id (re-frame/subscribe [:place-id])]
    (fn []
      (when place-id
        [:iframe {:width       "100%"
                  :height      450
                  :frameBorder 0
                  :src         (str "https://www.google.com/maps/embed/v1/place?key=AIzaSyCDbcLwR97CQ5xrMxYGWo5H1kL4tN3VJjQ&q=place_id:" @place-id)}]))))
