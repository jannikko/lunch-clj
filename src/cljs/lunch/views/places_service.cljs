(ns lunch.views.places-service
  (:require [re-frame.core :refer [dispatch]]
            [reagent.core :as reagent]))

(defn places-service-node-render
  "The places api needs a node to render to even when there is no map on the website"
  []
  [:div])

(defn places-service-node-did-mount
  "Instantiate the places service from the google api"
  [this]
  (dispatch [:initialize-places-service (reagent/dom-node this)]))

(defn places-service []
  (reagent/create-class {:reagent-render      places-service-node-render
                         :component-did-mount places-service-node-did-mount}))
