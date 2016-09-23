(ns lunch.views.detail
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [reagent.ratom :refer [reaction]]
            [lunch.views.components.place :refer [place-contact place-map]]
            [clojure.string :refer [split]]))

(defn detail-panel-render []
  (fn []
    [:div {:class "container"}
     [place-contact]
     [:div {:class "row pad"}
      [:div {:class "col-lg-12"}
       [:button {:class "btn btn-primary btn-lg btn-block" :on-click #(dispatch [:generate-lunch-session])} "Generate Lunch Session"]]]
     [:div {:class "row"}
      [:div {:class "col-lg-12"}
       [place-map]]]]))

(defn detail-panel-did-mount
  []
  (let [params (re-frame/subscribe [:url-params])]
    (dispatch [:initialize-detail-view @params])))


(defn detail-panel []
  (reagent/create-class {:reagent-render      detail-panel-render
                         :component-did-mount detail-panel-did-mount}))
