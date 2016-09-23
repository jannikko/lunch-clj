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
      (dispatch [:lunch.handlers.components.place/request-places-api])
      (assoc db :view {:place-id places-id}))))

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
