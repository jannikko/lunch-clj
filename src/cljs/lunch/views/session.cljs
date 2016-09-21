(ns lunch.views.session
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [reagent.ratom :refer [reaction]]
            [lunch.views.components.place :refer [place-map place-contact]]
            [clojure.string :refer [split]]))

(defn session-panel-did-mount []
  (let [params (re-frame/subscribe [:url-params])]
    (dispatch [:initialize-session-view @params])))

(defn add-stuff []
  (let [name-input (re-frame/subscribe [:lunch.subs.session/name-input])
        order-input (re-frame/subscribe [:lunch.subs.session/order-input])]
    (fn []
      [:div
       [:form {:on-submit #(do (.preventDefault %)
                               (dispatch [:lunch.handlers.session/update-session-handler]))}
        [:input {:type      "text" :id "name" :placeholder "Name" :value @name-input
                 :on-change #(dispatch [:lunch.handlers.session/name-input-changed (-> % .-target .-value)])}]
        [:input {:type      "text" :id "order" :placeholder "Order" :value @order-input
                 :on-change #(dispatch [:lunch.handlers.session/order-input-changed (-> % .-target .-value)])}]
        [:input {:type "submit"}]]])))

(defn session-entry
  [entry]
  [:div [:span (:name entry)] " " [:span (:lunch-order entry)]])

(defn session-state-view []
  (let [session-state (re-frame/subscribe [:session-state])]
    (fn []
      [:div (->> @session-state (sort-by :row) (reverse) (map session-entry))])))

(defn session-panel-render []
  (let [place-id (re-frame/subscribe [:place-id])]
    (fn []
      [:div
       [place-contact]
       [add-stuff]
       [session-state-view]
       [place-map place-id]
       ])))

(defn session-panel []
  (reagent/create-class {:reagent-render      session-panel-render
                         :component-did-mount session-panel-did-mount}))
