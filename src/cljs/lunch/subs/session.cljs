(ns lunch.subs.session
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :session-id
  (fn [db _]
    (reaction (get-in @db [:view :session-id]))))

(re-frame/register-sub
  :session-state
  (fn [db _]
    (reaction (get-in @db [:view :session-state]))))

(re-frame/register-sub
  ::name-input
  (fn [db _]
    (reaction (get-in @db [:view :name-input]))))

(re-frame/register-sub
  ::order-input
  (fn [db _]
    (reaction (get-in @db [:view :order-input]))))
