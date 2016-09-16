(ns lunch.subs.home
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :query
  (fn [db _]
    (reaction (get-in @db [:view :query]))))

(re-frame/register-sub
  :search-result
  (fn [db _]
    (reaction (get-in @db [:view :search-result]))))
