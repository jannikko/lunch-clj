(ns lunch.handlers.home
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [clojure.string :as string]))

(defn places-service-textsearch
  "Sends a request to the google places api"
  ([service query location]
   (when (not (string/blank? query))
     (.textSearch service (clj->js {"query" query "location" location})
                  #(dispatch [:handle-places-search-result %])))))

(re-frame/register-handler
  :reset-places-search-result
  (fn [db [_ _]]
    (update-in db [:view] dissoc :search-result)))


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
