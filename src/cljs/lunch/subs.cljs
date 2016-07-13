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
    (reaction (:query @db))))

(re-frame/register-sub
 :search-result
 (fn [db _]
   (reaction (:search-result @db))))

(re-frame/register-sub
 :url-params
 (fn [db _]
   (reaction (:url-params @db))))
