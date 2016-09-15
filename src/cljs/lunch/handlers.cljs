(ns lunch.handlers
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :as async :refer [<! >! put! chan]]
            [clojure.string :as string]
            [lunch.db :as db]))




(defn places-service-textsearch
  "Sends a request to the google places api"
  ([service query location]
   (when (not (string/blank? query))
     (.textSearch service (clj->js {"query" query "location" location})
                  #(dispatch [:handle-places-search-result %])))))

(defn places-service-detail
  "Sends a request to the google places api"
  ([service place-id]
   (when (some? place-id)
     (.getDetails service (clj->js {"placeId" place-id})
                  #(dispatch [:handle-places-detail-result %])))))


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

;; Google Maps Search

(re-frame/register-handler
  :initialize-places-service
  (fn [db [_ node]]
    (assoc db :places-service (js/google.maps.places.PlacesService. node))))

(re-frame/register-handler
  :request-places-service
  (fn [{:keys [places-service view location] :as db} [_ _]]
    (places-service-textsearch places-service (:query view) location)
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
    (let [places-id (:id params)]
      (dispatch [:request-places-textseach places-id])
      (dispatch [:api-menu/get-one :handle-menu-api-response places-id])
      (assoc db :view {:place-id places-id}))))

(re-frame/register-handler
  :handle-menu-api-response
  (fn [db [_ response]]
    (if (-> response :status (= 200))
      (-> db (assoc-in [:view :menu-link] (-> response :body :link)))
      db)))

(re-frame/register-handler
  :request-places-textseach
  (fn [{:keys [places-service view] :as db} [_ _]]
    (places-service-detail places-service (:place-id view))
    db))

(re-frame/register-handler
  :handle-link-upload-response
  (fn [db [_ response]]
    (if (-> response :status (= 201))
      (dispatch [:api-menu/get-one :handle-menu-api-response (get-in db [:view :place-id])])
      db)))

(re-frame/register-handler
  :menu-link-input-changed
  (fn [db [_ input]]
    (assoc-in db [:view :menu-link-input] input)))

(re-frame/register-handler
  :handle-places-detail-result
  (fn [db [_ result]]
    (assoc-in db [:view :detail-result] (js->clj result :keywordize-keys true))))

(re-frame/register-handler
  :menu-link-input-submit
  (fn [db _]
    (let [link (get-in db [:view :menu-link-input])
          place-id (get-in db [:view :place-id])]
      (if (some? link)
        (dispatch [:api-menu/upload-menu :handle-link-upload-response link place-id])
        db))))

