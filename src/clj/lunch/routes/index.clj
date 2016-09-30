(ns lunch.routes.index
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [config.core :refer [env]]
            [cheshire.core :as json]
            [hiccup.element :refer [javascript-tag]]))

(defn handler []
  (html5 [:head
          [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
          [:meta {:charset "utf-8"}]
          [:link {:type        "text/css"
                  :rel         "stylesheet"
                  :href        "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.4/css/bootstrap.min.css"
                  :integrity   "sha384-2hfp1SzUoho7/TsGGGDaFdsuuDL0LX2hnUp6VkX3CUQ2K4K+xjboZdsXyp4oUHZj"
                  :crossorigin "anonymous"}]
          [:link {:type        "text/css"
                  :rel         "stylesheet"
                  :href        "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"
                  :integrity   "sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp"
                  :crossorigin "anonymous"}]

          (include-css "css/site.css")]
         [:body
          [:div {:id "app"}]
          [:script {:src         "https://code.jquery.com/jquery-3.1.1.min.js"
                    :integrity   "sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
                    :crossorigin "anonymous"}]
          [:script {:src         "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.4/js/bootstrap.min.js"
                    :integrity   "sha384-VjEeINv9OSwtWFLAtmc4JCtEJXXBub00gtSnszmspDLCtC0I4z4nqz7rEFbIZLLU"
                    :crossorigin "anonymous"}]
          (include-js "https://maps.googleapis.com/maps/api/js?key=AIzaSyCDbcLwR97CQ5xrMxYGWo5H1kL4tN3VJjQ&libraries=places")
          (include-js "js/compiled/app.js")
          (javascript-tag (str "var SERVER_CONFIG = " (json/generate-string (select-keys env [:ws-protocol]))))
          (javascript-tag "lunch.core.init()")]))
