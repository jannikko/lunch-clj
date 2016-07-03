(ns lunch.handlers
    (:require [re-frame.core :as re-frame]
              [lunch.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))


(re-frame/register-handler
  :search-input-changed
  (fn [db [_ input]]
    (do
      ;;(request-places-service places-service input-text location)
      (assoc db :query input))))
