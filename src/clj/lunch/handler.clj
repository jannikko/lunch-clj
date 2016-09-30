(ns lunch.handler
  (:require [lunch.routes.menu :as menu-routes]
            [lunch.routes.session :as session-routes]
            [lunch.routes.index :as index-routes]
            [ring.util.response :refer [resource-response content-type]]
            [lunch.exceptions :refer [application-exception-type application-exception?]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as res]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token* wrap-anti-forgery]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]])
  (:import [clojure.lang ExceptionInfo]))

;; Write general tests that use ring/mock for handler
;; Use clojurewerkz/route-one for bidirectional routing
;; Refactor responses to records -> view (business) objects
;; Make most keywords namespaced (at least for client)

(def CSRF-HEADER "x-csrf-token")

(defn catch-all-handler
  "Adds CSRF Token to the response header of get requests"
  [handler]
  (fn [request]
    (try (handler request)
         (catch ExceptionInfo e (if (application-exception? e)
                                  (let [code (-> e ex-data :code)]
                                    {:status code :headers {} :body (.getMessage e)})
                                  (do (log/error (.getMessage e) request)
                                      (res/bad-request))))
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
    (GET "/" [] (index-routes/handler))
    (context "/api" []
      (context "/menu" [] (menu-routes/handler db))
      (context "/session" [] (session-routes/handler db)))
    (GET "/.well-known/acme-challenge/:id" (content-type (res/ok "DueOnp24BPCgweEKZ6M0JnzZ69bT647HhDQXpfBR7kk.7j2TCtjO_bcQAArJJmtV14fJys2UtMOaOze4G-grakk") "text/plain"))
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
