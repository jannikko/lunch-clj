(ns lunch.handler
  (:require [clojure.string :refer [blank?]]
            ;;[clojure.tools.logging :as log]
            [clojure.spec :as s]
            [ring.util.response :as res :refer [resource-response content-type]]
            [lunch.file-util :as futil]
            [lunch.exceptions :refer :all]
            [slingshot.slingshot :refer [throw+]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token* wrap-anti-forgery]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]))


(s/def ::id (complement blank?))
(s/def ::tempfile (complement blank?))
(s/def ::file (s/keys :req-un [::tempfile]))
(s/def ::file-upload-params (s/keys :req-un [::id ::file]))

(def CSRF-HEADER "x-csrf-token")

(defn place-download [id]
  (-> (res/response (str "You are viewing article: " id))
      (res/header CSRF-HEADER *anti-forgery-token*)
      (res/content-type "text/plain")))

(defn place-upload [params]
  (if (s/valid? ::file-upload-params params)
  (let [file (:file params) place-id (:id params)]
    (do
      ;(futil/save-file (:tempfile file) place-id)
      (res/response (str "You are viewing: " params))))
  (do 
    ;; Need to set up log4j
    (print (s/explain ::file-upload-params params))
    (throw+ (ApplicationException 400 "Something went wrong when uploading the file")))))

(defn api-routes [request]
  (routes
    (GET "/place/:id" [id] (place-download id))
    (GET "/place/:id/download" request (place-upload request))
      (POST "/place/:id/upload" {params :params} (place-upload params))))

(defroutes app-routes
  (GET "/" [] (content-type (resource-response "index.html" {:root "public"}) "text/html"))
  (context "/api" [request] (api-routes request))
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler (-> app-routes
                 (wrap-anti-forgery)
                 (wrap-keyword-params)
                 (wrap-multipart-params)
                 (wrap-params)
                 (wrap-session)))

(def dev-handler (-> handler wrap-reload))


