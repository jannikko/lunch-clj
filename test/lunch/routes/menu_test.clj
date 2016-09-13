(ns lunch.routes.menu-test
  (:import [java.io IOException File]
           [java.sql Connection])
  (:require [lunch.routes.menu :refer :all]
            [lunch.models.menu :as menu-model]
            [lunch.db :refer [new-database]]
            [lunch.exceptions :refer :all]
            [ring.mock.request :as mock]
            [lunch.file-util :as futil]
            [ring.util.response :as res]
            [slingshot.test]
            [clojure.test :refer :all]))

(defn response? [code res]
  (= (:status res) code))

(def test-file (File. "/tmp"))

(def db (new-database {:datasource "whatever"}))

(def file-response {:status  200
                    :headers {"Content-Length" "2089", "Last-Modified" "Mon, 05 Sep 2016 16:19:16 GMT"}
                    :body    test-file})

(def connection {:connection (reify Connection)})

(def valid-file-upload {:id "123" :file {:tempfile test-file}})

(deftest menu-routes
  (testing "menu-routes"
    (testing "menu-upload"
      ;; Test input validation
      (is (thrown? AssertionError (menu-upload nil db)))
      (is (thrown? AssertionError (menu-upload {:id 123 :file {:tempfile test-file}} db)))
      (is (thrown? AssertionError (menu-upload {:id nil :file {:tempfile test-file}} db)))
      (is (thrown? AssertionError (menu-upload {:id "123" :file {:tempfile nil}} db)))
      ;; File does not exist yet
      (with-redefs-fn {#'lunch.models.menu/insert-file-transactional (constantly true)
                       #'lunch.db/get-connection                     (constantly connection)}
        #(is (response? 201 (menu-upload valid-file-upload db))))
      ;; File does already exist
      (with-redefs-fn {#'lunch.models.menu/insert-file-transactional (constantly false)
                       #'lunch.db/get-connection                     (constantly connection)}
        #(is (response? 409 (menu-upload valid-file-upload db))))
      ;; Error while trying to insert the file 
      (with-redefs-fn {#'lunch.models.menu/insert-file-transactional (fn [t1 t2 t3] (throw (IOException. "Fails")))
                       #'lunch.db/get-connection                     (constantly connection)}
        #(is (thrown+? application-exception? (menu-upload {:id "123" :file {:tempfile test-file}} db)))) )
    (testing "menu-download"
      (is (thrown? AssertionError (menu-download nil db)))
      ;; Returns empty when not found in db
      (with-redefs-fn {#'lunch.models.menu/find-by-id (fn [id conn] ())
                       #'lunch.db/get-connection      (constantly connection)}
        #(is (response? 404 (menu-download "whatever" db))))
      (with-redefs-fn {#'lunch.models.menu/find-by-id (constantly "/a/file")
                       #'res/file-response            (constantly file-response)
                       #'lunch.db/get-connection      (constantly connection)}
        #(is (response? 200 (menu-download "12345" db)))
        ))))




