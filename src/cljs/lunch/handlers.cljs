(ns lunch.handlers
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [lunch.handlers.home]
            [lunch.handlers.detail]
            [lunch.handlers.places-service]
            [lunch.handlers.session]
            [lunch.db :as db]))

;; Initialize the default db

(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))


;; Routing handler

(re-frame/register-handler
  :set-active-panel
  (fn [db [_ active-panel params]]
    (assoc db :active-panel active-panel :url-params params)))

;; Geolocation

(re-frame/register-handler
  :location-request
  (fn [db _]
    (let [geolocation (.-geolocation js/navigator)]
      (.getCurrentPosition geolocation
                           (fn [pos] (let [coords (. pos -coords)]
                                       (dispatch [:location-response (clj->js
                                                                       {:lat (. coords -latitude)
                                                                        :lng (. coords -longitude)})]))))) db))

(re-frame/register-handler
  :location-response
  (fn [db [_ location]]
    (dispatch [:request-places-service])
    (assoc db :location location)))
