(ns lunch.api.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [cljs-http.client :as http]
            [cljs.core.async :as async :refer [<! >! put! chan]]))


(defn extract-csrf
  [response]
    (-> response :headers (get "csrf-token")))

(defn assoc-csrf
  [request csrf-token]
  (assoc-in request [:args :headers "csrf-token"] csrf-token))

(defn wrap-get-handler
  [handler]
  (fn [db [_ callback-handler & args]]
    (go
      (let [request (handler db args)
            response (<! (http/get (:url request) (:params request)))]
          (dispatch [:api-handlers/handle-get-response response callback-handler])))
    db))

(defn wrap-post-handler
  [handler]
  (fn [db [_ callback-handler & args]]
    (go
      (let [request (handler db args)
            request-with-csrf (assoc-csrf request (:csrf-token db))
            response (<! (http/post (:url request-with-csrf) (:params request-with-csrf)))]
        (dispatch [:api-handlers/handle-post-response response callback-handler])))
    db))

(re-frame/register-handler
 :api-handlers/handle-get-response
 (fn [db [_ response callback-handler]]
   (dispatch [callback-handler response])
   (assoc db :csrf-token (extract-csrf response))))

(re-frame/register-handler
 :api-handlers/handle-post-response
 (fn [db [_ response callback-handler]]
   (dispatch [callback-handler response])
   db))
