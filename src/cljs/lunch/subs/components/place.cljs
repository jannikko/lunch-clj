(ns lunch.subs.components.place
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :place-contact
  (fn [db _]
    (reaction (get-in @db [:view :place-contact]))))
