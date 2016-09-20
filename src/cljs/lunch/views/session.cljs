(ns lunch.views.session
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [reagent.ratom :refer [reaction]]
            [clojure.string :refer [split]]))

(defn session-panel-did-mount []
  (let [params (re-frame/subscribe [:url-params])]
    (dispatch [:initialize-session-view @params])))

(defn add-stuff []
  (let [name-input (re-frame/subscribe [:lunch.subs.session/name-input])
        order-input (re-frame/subscribe [:lunch.subs.session/order-input])]
  (fn []
    [:div
     [:input {:type      "text" :id "name" :placeholder "Name" :value @name-input
              :on-change #(dispatch [:lunch.handlers.session/name-input-changed (-> % .-target .-value)])}]
     [:input {:type "text" :id "order" :placeholder "Order" :value @order-input
              :on-change #(dispatch [:lunch.handlers.session/order-input-changed (-> % .-target .-value)])}]
     [:button {:on-click #(dispatch [:lunch.handlers.session/update-session-handler])}]])))

(defn session-state-view []
  (let [session-state (re-frame/subscribe [:session-state])]
    (fn []
      [:div (str @session-state)])))

(defn session-panel-render []
  (let [session-id (re-frame/subscribe [:session-id])]
    (fn []
      [:div [:h1 (str "Lunch session " @session-id)]
       [add-stuff]
       [session-state-view]])))

(defn session-panel []
  (reagent/create-class {:reagent-render      session-panel-render
                         :component-did-mount session-panel-did-mount}))
