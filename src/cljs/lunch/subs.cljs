(ns lunch.subs
    (:require-macros [reagent.ratom :refer [reaction]])

    (:require [re-frame.core :as re-frame]
              [reagent.core :as reagent]))

(re-frame/register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(re-frame/register-sub
  :query
  (fn [db _]
    (reaction (get-in @db [:view :query]))))

(re-frame/register-sub
 :search-result
 (fn [db _]
   (reaction (get-in @db [:view :search-result]))))

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
 :url-params
 (fn [db _]
   (reaction (:url-params @db))))
