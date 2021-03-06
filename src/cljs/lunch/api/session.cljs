(ns lunch.api.session
  (:require [lunch.api.handlers :refer [wrap-get-handler wrap-post-handler]]
            [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close!]]
            [re-frame.core :as re-frame :refer [dispatch]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn create-session
  [_ place-id]
  {:url    "api/session/generate"
   :params {:json-params {"place-id" place-id}}})

(defn session-metadata
  [_ session-id]
  {:url    (str "api/session/" session-id)})

(re-frame/register-handler
  :api-session/create-session
  (wrap-post-handler create-session))

(re-frame/register-handler
  :api-session/session-metadata
  (wrap-get-handler session-metadata))

(re-frame/register-handler
  :api-session/connect
  (fn
    [db [_ callback session-id]]
    (go (let [{:keys [ws-channel error]} (<! (ws-ch (str (aget js/SERVER_CONFIG "ws-protocol") "://" (.. js/window -location -host) "/api/session/" session-id "/connect")))]
        (if-not error
          (dispatch [callback ws-channel]))))
    db))
