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

(re-frame/register-handler
  :api-session/create-session
  (wrap-post-handler create-session))

(re-frame/register-handler
  :api-session/connect
  (fn
    [db [_ callback session-id]]
    ;; TODO Move this to configuration
    (go (let [{:keys [ws-channel error]} (<! (ws-ch (str "ws://localhost:3500/api/session/" session-id "/connect")))]
        (if-not error
          (dispatch [callback ws-channel])
          (js/console.log "Error:" (pr-str error)))))
    db))
