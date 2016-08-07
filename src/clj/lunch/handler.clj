(ns lunch.handler
  (:require [ring.util.response :as res :refer [resource-response content-type]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.middleware.reload :refer [wrap-reload]]))

(def CSRF-HEADER "x-csrf-token")

(defn place-download [id]
  (-> (res/response (str "You are viewing article: " id))
   (res/header CSRF-HEADER *anti-forgery-token*)
   (res/content-type "text/plain")))

(defn place-upload [request]
  (println request)
  (res/response (str "You are viewing article: " request)))

(defn api-routes [request]
  (routes
   (GET "/place/:id" [id] (place-download id))
   (GET "/place/:id/download" request (place-upload request))
   (POST "/place/:id/upload" request (place-upload request))
   ))

(defroutes app-routes
  (GET "/" [] (content-type (resource-response "index.html" {:root "public"}) "text/html"))
  (context "/api" [request] (api-routes request))
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler (-> app-routes
              (wrap-defaults site-defaults)))

(def dev-handler (-> handler wrap-reload))


