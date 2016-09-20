(ns lunch.handlers.session
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [secretary.core :refer [defroute]])
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :refer [<! >! put! chan]]
            [lunch.routes :refer [session-route]]
            [lunch.handlers.home]))

(defn listen-connection
  [connection]
  (go-loop []
    (let [session (<! connection)]
      (when session (dispatch [:handle-server-session-update session]))
      (recur))))

(defn send-update
  [connection update]
  (go (>! connection update)))

(re-frame/register-handler
  :handle-server-session-update
  (fn [db [_ session]]
    (.log js/console "session" session)
    (assoc-in db [:view :session-state] session)))

(re-frame/register-handler
  :handle-session-connection
  (fn [db [_ connection]]
    (listen-connection connection)
    (assoc-in db [:view :connection] connection)))

(re-frame/register-handler
  ::name-input-changed
  (fn [db [_ input]]
    (assoc-in db [:view :name-input] input)))

(re-frame/register-handler
  ::order-input-changed
  (fn [db [_ input]]
    (assoc-in db [:view :order-input] input)))

(re-frame/register-handler
  ::update-session-handler
  (fn [db _]
    (let [connection (-> db :view :connection)]
      (when connection (send-update connection {:name (-> db :view :name-input)
                                                :lunch-order (-> db :view :order-input)}))
      db)))

(re-frame/register-handler
  :initialize-session-view
  (fn [db [_ params]]
    (let [session-id (:id params)]
      (dispatch [:api-session/connect :handle-session-connection session-id])
      (assoc db :view {:session-id session-id}))))

