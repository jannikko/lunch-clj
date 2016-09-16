(ns lunch.handlers.detail
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :refer [<! >! put! chan]]
            [lunch.handlers.home]))

(defn places-service-detail
  "Sends a request to the google places api"
  ([service place-id]
   (when (some? place-id)
     (.getDetails service (clj->js {"placeId" place-id})
                  #(dispatch [:handle-places-detail-result %])))))

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

