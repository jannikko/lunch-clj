(ns lunch.api.session
  (:require [lunch.api.handlers :refer [wrap-get-handler wrap-post-handler]]
            [re-frame.core :as re-frame :refer [dispatch]]))

(defn lunch [id] (str "api/lunch/" id))

(defn create-session
  [_ place-id]
  {:url (str (lunch place-id) "/create-session")})

(re-frame/register-handler
  :api-session/create-session
  (wrap-post-handler create-session))
