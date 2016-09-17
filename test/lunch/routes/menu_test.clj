(ns lunch.routes.menu-test
  (:import [java.io IOException]
           [clojure.lang ExceptionInfo]
           [java.sql Connection])
  (:require [lunch.routes.menu :refer :all]
            [lunch.models.menu :as menu-model]
            [lunch.db :refer [new-database]]
            [lunch.exceptions :refer :all]
            [slingshot.test]
            [lunch.test-fixtures :refer [general-fixtures]]
            [clojure.test :refer :all]))

(general-fixtures)

(defn response? [code res]
  (= (:status res) code))

(def db (new-database {:datasource "whatever"}))
(def good-request {:params {:id "123145"} :body {:link "http://example.com"}})
(def bad-request1 {:params {} :body {:link "http://example.com"}})
(def bad-request2 {:params {:id "123145"} :body {:link "invalidurl"}})
(def bad-request3 {:params {:id "123145"} :body {}})

(def connection {:connection (reify Connection)})

(deftest menu-routes
  (testing "menu-routes"
    (testing "menu-upload"
      ;; Test input validation
      (with-redefs-fn {#'lunch.models.menu/insert-link (constantly true)
                       #'lunch.db/get-connection       (constantly connection)}
        #(do (is (response? 201 (menu-upload good-request db)))
             (is (thrown? ExceptionInfo (menu-upload nil db)))
             (is (thrown? ExceptionInfo (menu-upload bad-request1 db)))
             (is (thrown? ExceptionInfo (menu-upload bad-request2 db)))
             (is (thrown? ExceptionInfo (menu-upload bad-request3 db)))) )
      ;; Link does already exist
      (with-redefs-fn {#'lunch.models.menu/insert-link (constantly false)
                       #'lunch.db/get-connection       (constantly connection)}
        #(is (response? 409 (menu-upload good-request db))))
      ;; Error while trying to insert the Link
      (with-redefs-fn {#'lunch.models.menu/insert-link (fn [t1 t2 t3] (throw (IOException. "Fails")))
                       #'lunch.db/get-connection       (constantly connection)}
        #(is (thrown+? application-exception? (menu-upload good-request db)))))
    (testing "menu-download"
      (is (thrown? ExceptionInfo (menu-download nil db)))
      ;; Returns empty when not found in db
      (with-redefs-fn {#'lunch.models.menu/find-by-id (fn [id conn] ())
                       #'lunch.db/get-connection      (constantly connection)}
        #(is (response? 404 (menu-download "whatever" db))))
      (with-redefs-fn {#'lunch.models.menu/find-by-id (constantly (seq {:link "www.example.com"}))
                       #'lunch.db/get-connection      (constantly connection)}
        #(is (response? 200 (menu-download "12345" db)))
        ))))




