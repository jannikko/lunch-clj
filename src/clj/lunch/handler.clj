(ns lunch.handler
  (:require [ring.util.response :as res :refer [resource-response]]
            [bidi.ring :refer [make-handler]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn index-handler
  [request]
  (resource-response "index.html" {:root "public"}))

(defn article-handler
  [{:keys [route-params]}]
  (res/response (str "You are viewing article: " (:id route-params))))

(def handler
  (make-handler ["/" {"" index-handler
                      "api/" {"place" article-handler}}]))

(def dev-handler (-> handler wrap-reload))


