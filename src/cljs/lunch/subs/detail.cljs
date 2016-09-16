(ns lunch.subs.detail
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :place-id
  (fn [db _]
    (reaction (get-in @db [:view :place-id]))))

(re-frame/register-sub
  :menu-link
  (fn [db _]
    (reaction (get-in @db [:view :menu-link]))))

(re-frame/register-sub
  :menu-link-input
  (fn [db _]
    (reaction (get-in @db [:view :menu-link-input]))))

(re-frame/register-sub
  :detail-result
  (fn [db _]
    (reaction (get-in @db [:view :detail-result]))))
