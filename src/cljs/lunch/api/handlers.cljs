(ns lunch.api.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :as async :refer [<! >! put! chan]]))


(defn extract-csrf
  [response]
    (-> response :headers (get "csrf-token")))

(defn GET-handler
  [handler]
  (fn [db [_ callback-handler & args]]
    (go
      (let [response (<! (handler db args))]
          (dispatch [:api-handlers/handle-get-response response callback-handler])))
    db))

(re-frame/register-handler
 :api-handlers/handle-get-response
 (fn [db [_ response callback-handler]]
   (dispatch [callback-handler response])
   (assoc db :csrf-token (extract-csrf response))))
