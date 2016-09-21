(ns lunch.views.detail
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [reagent.ratom :refer [reaction]]
            [lunch.views.components.place :refer [place-contact place-map]]
            [clojure.string :refer [split]]))

(defn detail-panel-render []
  (let [menu-link (re-frame/subscribe [:menu-link])
        menu-link-input (re-frame/subscribe [:menu-link-input])]
    (fn []
      [:div [place-contact]
       [:div [:a {:href "#/"} "go to Home Page"]]
       [:div [:label {:for "menu-link"} "Menu Link: "]
        (if-not (some? @menu-link) [:input {:type         "text"
                                            :value        @menu-link-input
                                            :id           "menu-link"
                                            :on-change    #(dispatch [:menu-link-input-changed (-> % .-target .-value)])
                                            :on-key-press #(when (-> % .-charCode (= 13))
                                                            (dispatch [:menu-link-input-submit])
                                                            (-> % .-preventDefault))}]
                                   [:a {:href @menu-link :id "menu-link"} @menu-link])]
       [:button {:on-click #(dispatch [:generate-lunch-session])} "Generate Lunch Session"]
       [place-map]])))

(defn detail-panel-did-mount
  []
  (let [params (re-frame/subscribe [:url-params])]
    (dispatch [:initialize-detail-view @params])))


(defn detail-panel []
  (reagent/create-class {:reagent-render      detail-panel-render
                         :component-did-mount detail-panel-did-mount}))
