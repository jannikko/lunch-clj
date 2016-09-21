(ns lunch.handlers.detail
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :refer [<! >! put! chan]]
            [lunch.routes :refer [session-route]]
            [lunch.handlers.home]))

(re-frame/register-handler
  :initialize-detail-view
  (fn [db [_ params]]
    (let [places-id (:id params)]
      (dispatch [:api-menu/get-one :handle-menu-api-response places-id])
      (dispatch [:lunch.handlers.components.place/request-places-api])
      (assoc db :view {:place-id places-id}))))

(re-frame/register-handler
  :handle-menu-api-response
  (fn [db [_ response]]
    (if (-> response :status (= 200))
      (-> db (assoc-in [:view :menu-link] (-> response :body :link)))
      db)))

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
  :menu-link-input-submit
  (fn [db _]
    (let [link (get-in db [:view :menu-link-input])
          place-id (get-in db [:view :place-id])]
      (if (some? link)
        (dispatch [:api-menu/upload-menu :handle-link-upload-response link place-id])
        db))))

(re-frame/register-handler
  :handle-session-response
  (fn [db [_ response]]
    (when (-> response :status (= 201))
      (.assign js/location (session-route {:id (get-in response [:headers "location"])})))
    db))

(re-frame/register-handler
  :generate-lunch-session
  (fn [db _]
      (dispatch [:api-session/create-session :handle-session-response (get-in db [:view :place-id])])
    db))
