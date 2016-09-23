(ns lunch.views.home
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [lunch.routes :refer [detail-route]]
            [clojure.string :refer [split]]
            ))

(defn search-result-entry
  [{:keys [place_id name formatted_address]}]
  [:div
   [:a {:class "list-group-item" :href (detail-route {:id place_id})} name " " formatted_address]])

(defn search-results []
  (let [result (re-frame/subscribe [:search-result])]
    (fn []
      (into [:div {:class "list-group"}] (map search-result-entry @result)))))


(defn home-panel-did-mount []
  (let [params (re-frame/subscribe [:url-params])]
    (dispatch [:initialize-home-view @params])))

(defn home-panel-render []
  (let [query (re-frame/subscribe [:query])]
    (fn []
      [:div {:class "jumbotron vertical-center" :id "home"}
       [:div {:class "container"}
        [:div {:class "input-group"}
         [:input {:class "form-control" :placeholder "Where would you like to eat today?" :type "text" :value @query :on-change #(dispatch [:search-input-changed (-> % .-target .-value)])}]
         [:span {:class "input-group-btn"}
          [:button {:class "btn btn-default" :type "button" :value "Location" :on-click #(dispatch [:location-request])} "My Location"]]]
        [search-results]]]
      )))


(defn home-panel []
  (reagent/create-class {:reagent-render      home-panel-render
                         :component-did-mount home-panel-did-mount}))

