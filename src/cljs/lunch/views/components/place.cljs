(ns lunch.views.components.place
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.ratom :refer [reaction]]
            [reagent.core :as reagent]
            [clojure.string :refer [split]]))

(defn- place-contact
  []
  (let [place-contact (re-frame/subscribe [:place-contact])
        name (reaction (:name @place-contact))
        phone-number (reaction (:international_phone_number @place-contact))
        website (reaction (:website @place-contact))]
    (fn []
      [:div [:h1 @name]
       [:div [:span @phone-number] [:a {:href @website} @website]]])))

(defn place-map
  []
  (let [place-id (re-frame/subscribe [:place-id])]
    (fn []
      [:div [:iframe {:width       "100%"
                      :height      450
                      :frameborder 0
                      :src         (str "https://www.google.com/maps/embed/v1/place?key=AIzaSyCDbcLwR97CQ5xrMxYGWo5H1kL4tN3VJjQ&q=place_id:" @place-id)}]])))
