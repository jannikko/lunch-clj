(ns lunch.handler
  (:require [ring.util.response :as res :refer [resource-response content-type]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token* wrap-anti-forgery]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]))

(def CSRF-HEADER "x-csrf-token")

(defn place-download [id]
  (-> (res/response (str "You are viewing article: " id))
   (res/header CSRF-HEADER *anti-forgery-token*)
   (res/content-type "text/plain")))

(defn place-upload [request]
  (res/response (str "You are viewing: " request)))

;; TODO Implement saving files
(defn save-place-upload-to-fs
  [{:keys [filename content-type stream] :as args}]
  "Filepath")

(defn api-routes [request]
  (routes
   (GET "/place/:id" [id] (place-download id))
   (GET "/place/:id/download" request (place-upload request))
   (wrap-multipart-params
    (POST "/place/:id/upload" {params :params} (place-upload params))
    {:store save-place-upload-to-fs})))

(defroutes app-routes
  (GET "/" [] (content-type (resource-response "index.html" {:root "public"}) "text/html"))
  (context "/api" [request] (api-routes request))
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler (-> app-routes
                 (wrap-anti-forgery)
                 (wrap-params)
                 (wrap-session)))

(def dev-handler (-> handler wrap-reload))


