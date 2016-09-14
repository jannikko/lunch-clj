(ns lunch.handler
  (:require [lunch.routes.menu :as menu-routes]
            [ring.util.response :refer [resource-response content-type]]
            [lunch.exceptions :refer [application-exception-type]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.tools.logging :as log]
            [slingshot.slingshot :refer [throw+ try+]]
            [ring.util.http-response :as res]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token* wrap-anti-forgery]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]))

;; Write general tests that use ring/mock for handler
;; Respond with json
;; Use clojurewerkz/route-one for bidirectional routing

(def CSRF-HEADER "x-csrf-token")

(defn catch-all-handler
  "Adds CSRF Token to the response header of get requests"
  [handler]
  (fn [request]
    (try+ (handler request)
          (catch [:type application-exception-type] {:keys [code message]}
            {:status code :headers {} :body message})
          (catch AssertionError e (do (log/error (.getMessage e) request)
                                      (res/bad-request)))
          (catch Exception e (do (log/error (.getMessage e) request)
                                 (res/internal-server-error))))))


(defn add-csrf-token
  "Adds CSRF Token to the response header of get requests"
  [app]
  (fn [request]
    (let [response (app request)]
      (if (= (:request-method request) :get)
        (res/header response CSRF-HEADER *anti-forgery-token*)
        response))))

(defn app-routes
  [db]
  (routes
    (GET "/" [] (content-type (resource-response "index.html" {:root "public"}) "text/html"))
    (context "/api" []
      (context "/menu" [] (menu-routes/handler db)))
    (route/resources "/")
    (route/not-found "Page not found")))

(defn handler
  [db]
  (-> (app-routes db)
      ;(catch-all-handler)
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-json-response)
      (add-csrf-token)
      ;(wrap-anti-forgery)
      (wrap-keyword-params)
      (wrap-multipart-params)
      (wrap-params)
      (wrap-session)))
