(ns lunch.handler
  (:require [ring.util.response :as res :refer [resource-response content-type]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn place-download [id]
  (res/response (str "You are viewing article: " id)))

(defn place-upload [request]
  (res/response (str "You are viewing article: " request)))

(defn api-routes [request]
  (routes
   (GET "/place/:id" [id] (place-download id))
   (POST "/place/:id" request (place-upload request))
   ))

(defroutes app-routes
  (GET "/" [] (content-type (resource-response "index.html" {:root "public"}) "text/html"))
  (context "/api" [request] (api-routes request))
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler (->
              app-routes
              (wrap-defaults api-defaults)
              ))

(def dev-handler (-> handler wrap-reload))


