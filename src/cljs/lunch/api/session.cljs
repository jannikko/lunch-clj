(ns lunch.api.session
  (:require [lunch.api.handlers :refer [wrap-get-handler wrap-post-handler]]
            [re-frame.core :as re-frame :refer [dispatch]]))

(defn create-session
  [_ place-id]
  {:url "api/session/generate"
   :params {:json-params {"id" place-id}}})

(re-frame/register-handler
  :api-session/create-session
  (wrap-post-handler create-session))
