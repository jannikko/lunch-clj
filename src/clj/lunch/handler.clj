(ns lunch.handler
  (:require [ring.util.response :as res :refer [resource-response]]
            [bidi.ring :refer [make-handler files]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn index-handler
  [request]
  (resource-response "index.html" {:root "public"}))

(defn article-handler
  [{:keys [route-params]}]
  (res/response (str "You are viewing article: " (:id route-params))))

(defn resource-handler
  [request]
  (resource-response (:uri request) {:root "public"}))

(def routing-table
  ["/" {"" :index
        "api/" {["place/" :id] :api-place}}])

(def handler-map
  {:index index-handler
   :api-place article-handler
   })

(def routes-handler
  (make-handler routing-table handler-map))

(def handler
  (-> routes-handler
      (wrap-resource "public")))

(def dev-handler (-> handler wrap-reload))


