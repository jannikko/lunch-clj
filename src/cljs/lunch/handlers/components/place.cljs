(ns lunch.handlers.components.place
  (:require [re-frame.core :as re-frame :refer [dispatch]]))

(defn request-place-contact
  "Sends a request to the google places api"
  ([service place-id]
   (when (some? place-id)
     (.getDetails service (clj->js {"placeId" place-id})
                  #(dispatch [::handle-place-contact-result %])))))

(re-frame/register-handler
  ::handle-place-contact-result
  (fn [db [_ result]]
    (assoc-in db [:view :place-contact] (js->clj result :keywordize-keys true))))

(re-frame/register-handler
  ::request-places-api
  (fn [{:keys [places-service] :as db} _]
    (request-place-contact places-service (-> db :view :place-id))
    db))

