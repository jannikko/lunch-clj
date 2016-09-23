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
    (let [place-id (-> db :view :place-id)]
      (dispatch [:api-menu/get-one ::handle-menu-api-response place-id])
      (request-place-contact places-service place-id))
    db))

(re-frame/register-handler
  ::handle-menu-api-response
  (fn [db [_ response]]
    (if (-> response :status (= 200))
      (-> db (assoc-in [:view :menu-link] (-> response :body :link)))
      db)))

(re-frame/register-handler
  ::handle-link-upload-response
  (fn [db [_ response]]
    (if (-> response :status (= 201))
      (dispatch [:api-menu/get-one ::handle-menu-api-response (get-in db [:view :place-id])])
      db)))

(re-frame/register-handler
  :menu-link-input-changed
  (fn [db [_ input]]
    (assoc-in db [:view :menu-link-input] input)))

(re-frame/register-handler
  :menu-link-input-submit
  (fn [db _]
    (let [link (get-in db [:view :menu-link-input])
          place-id (get-in db [:view :place-id])]
      (if (some? link)
        (dispatch [:api-menu/upload-menu ::handle-link-upload-response link place-id])
        db))))
