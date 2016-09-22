(ns lunch.routes.index
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [config.core :refer [env]]
            [hiccup.element :refer [javascript-tag]]))

(defn handler []
  (html5 [:head
          [:meta {:charset "utf-8"}]
          (include-css "css/site.css")]
         [:body
          [:div {:id "app"}]
          (include-js "https://maps.googleapis.com/maps/api/js?key=AIzaSyCDbcLwR97CQ5xrMxYGWo5H1kL4tN3VJjQ&libraries=places")
          (include-js "js/compiled/app.js")
          (javascript-tag "lunch.core.init()")]))
