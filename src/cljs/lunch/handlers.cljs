(ns lunch.handlers
    (:require [re-frame.core :as re-frame :refer [dispatch]]
              [clojure.string :as string]
              [lunch.db :as db]))


(defn request-places-service
  "Sends a request to the google places api"
  ([service query location]
   (when (not (string/blank? query))
     (.textSearch service (clj->js {"query" query "location" location})
                  #(dispatch [:handle-places-search-result %])))))


(re-frame/register-handler
 :location-request
 (fn [db _]
  (let [geolocation (.-geolocation js/navigator)]
    (.getCurrentPosition geolocation
                         (fn [pos] (let [coords (. pos -coords)]
                                     (dispatch [:location-response (clj->js
                                                  {:lat (. coords -latitude)
                                                   :lng (. coords -longitude)})])))))db))

(re-frame/register-handler
 :location-response
 (fn [db [_ location]]
 (assoc db :location location)))

(re-frame/register-handler
 :handle-places-search-result
 (fn [db [_ result]]
   (assoc db :search-result (js->clj result :keywordize-keys true))))

(re-frame/register-handler
 :reset-places-search-result
 (fn [db [_ _]]
   (dissoc db :search-result)))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel params]]
   (assoc db :active-panel active-panel :url-params params)))

(re-frame/register-handler
 :search-input-changed
 (fn [db [_ input]]
   (if (string/blank? input)
     (dispatch [:reset-places-search-result])
     (dispatch [:request-places-service]))
     (assoc db :query input)))

(re-frame/register-handler
 :request-places-service
 (fn [{:keys [places-service query location] :as db} [_ _]]
   (request-places-service places-service query location)
   db))

(re-frame/register-handler
 :initialize-places-service
 (fn [db [_ node]]
   (assoc db :places-service (js/google.maps.places.PlacesService. node))))



