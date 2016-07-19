(ns lunch.handler
  (:require [ring.util.response :as res :refer [resource-response]]
            [bidi.ring :refer [make-handler files]]
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
        "api/" {["place/" :id] :api-place}
        true :resources}])

(def handler-map
  {:index index-handler
   :resources resource-handler
   :api-place article-handler
   })

(def handler
  (make-handler routing-table handler-map))

(def dev-handler (-> handler wrap-reload))


