(ns lunch.handlers.places-service
  (:require [re-frame.core :as re-frame :refer [dispatch]]))

(re-frame/register-handler
  :initialize-places-service
  (fn [db [_ node]]
    (assoc db :places-service (js/google.maps.places.PlacesService. node))))


