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
      [:div {:class "row"}
       [:div {:class "col-lg-12"}
        [:form {:class "input-group input-group-lg" :on-submit #(do (.preventDefault %) (dispatch [:lunch.handlers.session/update-session-handler]))}
         [:div {:class "row row-no-padding"}
          [:div {:class "col-lg-5"}
           [:input {:type      "text" :id "name" :placeholder "Name" :value @name-input
                    :class     "form-control input-lg"
                    :on-change #(dispatch [:lunch.handlers.session/name-input-changed (-> % .-target .-value)])}]]
          [:div {:class "col-lg-5"}
           [:input {:type      "text" :id "order" :placeholder "Order" :value @order-input
                    :class     "form-control input-lg"
                    :on-change #(dispatch [:lunch.handlers.session/order-input-changed (-> % .-target .-value)])}]]
          [:div {:class "col-lg-2"}
           [:div {:class "form-group"}
            [:input {:type "submit" :class "btn btn-primary btn-block"}]]]]]]])))

(defn session-entry
  [entry]
  [:div {:class "row row-no-padding"}
   [:div {:class "col-lg-5"} [:div {:class "card card-block"} [:p {:class "card-text"} (:name entry)]]]
   [:div {:class "col-lg-5"} [:div {:class "card card-block"} [:p {:class "card-text"} (:lunch-order entry)]]]
   [:div {:class "col-lg-2"}]])

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
