(ns lunch.routes.menu-test
  (:import (java.io IOException File))
  (:require [lunch.routes.menu :refer :all]
            [lunch.models.menu :as menu-model]
            [lunch.exceptions :refer :all]
            [ring.mock.request :as mock]
            [lunch.file-util :as futil]
            [ring.util.response :as res]
            [slingshot.test]
            [clojure.test :refer :all]))

(defn response? [code res]
  (= (:status res) code))

(def test-file (File. "/tmp"))

(def file-response {:status 200 
                    :headers {"Content-Length"  "2089",  "Last-Modified"  "Mon, 05 Sep 2016 16:19:16 GMT"} 
                    :body test-file})

(def valid-file-upload {:id "123" :file {:tempfile test-file}})

(deftest menu-routes
  (testing "menu-routes"
    (testing "menu-upload"
      ;; Test input validation
      (is (thrown+? application-exception? (menu-upload nil)))
      (is (thrown+? application-exception? (menu-upload {:id 123 :file {:tempfile test-file}})))
      (is (thrown+? application-exception? (menu-upload {:id nil :file {:tempfile test-file}})))
      (is (thrown+? application-exception? (menu-upload {:id "123" :file {:tempfile nil}})))
      ;; File does not exist yet
      (with-redefs-fn {#'lunch.models.menu/insert-file (constantly true)}
        #(is (response? 201 (menu-upload valid-file-upload))))
      ;; File does already exist
      (with-redefs-fn {#'lunch.models.menu/insert-file (constantly false)}
        #(is (response? 409 (menu-upload valid-file-upload))))
      ;; Error while trying to insert the file 
      (with-redefs-fn {#'lunch.models.menu/insert-file (fn [t1 t2 t3] (throw (IOException. "Fails")))}
        #(is (thrown+? application-exception? (menu-upload {:id "123" :file {:tempfile test-file}}))))
      )
    (testing "menu-upload"
      (is (thrown+? application-exception? (menu-download nil)))
      ;; Returns empty when not found in db
      (with-redefs-fn {#'lunch.models.menu/find-by-id (fn [id] ())}
        #(is (response? 404 (menu-download "whatever"))))
      (with-redefs-fn {#'lunch.models.menu/find-by-id (constantly "/a/file") 
                       #'res/file-response (constantly file-response)}
        #(is (response? 200 (menu-download "12345")))
      ))))




