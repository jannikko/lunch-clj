(ns lunch.handlers
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :as async :refer [<! >! put! chan]]
            [clojure.string :as string]
            [lunch.routes :as routes]
            [lunch.api.place :as place-api]
            [lunch.db :as db]))




(defn request-places-service
  "Sends a request to the google places api"
  ([service query location]
   (when (not (string/blank? query))
     (.textSearch service (clj->js {"query" query "location" location})
                  #(dispatch [:handle-places-search-result %])))))


;; Initialize the default db

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
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
                                                   :lng (. coords -longitude)})])))))db))

(re-frame/register-handler
 :location-response
 (fn [db [_ location]]
   (dispatch [:request-places-service])
   (assoc db :location location)))

;; Google Maps Search

(re-frame/register-handler
 :initialize-places-service
 (fn [db [_ node]]
   (assoc db :places-service (js/google.maps.places.PlacesService. node))))

(re-frame/register-handler
 :request-places-service
 (fn [{:keys [places-service view location] :as db} [_ _]]
   (request-places-service places-service (:query view) location)
   db))

(re-frame/register-handler
 :handle-places-search-result
 (fn [db [_ result]]
   (assoc-in db [:view :search-result] (js->clj result :keywordize-keys true))))

(re-frame/register-handler
 :reset-places-search-result
 (fn [db [_ _]]
   (update-in db [:view] dissoc :search-result)))

;; Home view handlers

(re-frame/register-handler
 :initialize-home-view
 (fn [db [_ params]]
   (assoc db :view {:query "" :search-result []})))

(re-frame/register-handler
 :search-input-changed
 (fn [db [_ input]]
   (if (string/blank? input)
     (dispatch [:reset-places-search-result])
     (dispatch [:request-places-service]))
   (assoc-in db [:view :query] input)))

;; Detail view handlers

(re-frame/register-handler
 :initialize-detail-view
 (fn [db [_ params]]
   (dispatch [:api-place/get-one :handle-place-api-response])
   (assoc-in db [:view :place-id] (:id params))))


(re-frame/register-handler
 :handle-place-api-response
 (fn [db [_ response]]
   (.log js/console response)
   db))

(re-frame/register-handler
 :handle-file-upload-response
 (fn [db [_ response]]
   (.log js/console (:body response))
   db))


(re-frame/register-handler
 :handle-file-submit
 (fn [db [_ files]]
     (when files
       (dispatch [:api-place/upload-place :handle-file-upload-response (aget files 0)]))
   db))
