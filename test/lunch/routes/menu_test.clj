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

(deftest menu-routes
  (testing "menu-routes"
    (testing "menu-upload"
      ;; Test input validation
      (is (thrown+? application-exception? (menu-upload nil)))
      (is (thrown+? application-exception? (menu-upload {:id 123 :file {:tempfile test-file}})))
      (is (thrown+? application-exception? (menu-upload {:id nil :file {:tempfile test-file}})))
      (is (thrown+? application-exception? (menu-upload {:id "123" :file {:tempfile nil}})))
      ;; Mock save-file function to return a path
      (with-redefs-fn {#'lunch.file-util/save-file (fn [t1 t2] (str "/bin/lib/123.pdf"))
                       #'lunch.models.menu/exists? (fn [id] false)
                       #'lunch.models.menu/insert! (fn [params conn] ())}
        #(is (response? 201 (menu-upload {:id "123" :file {:tempfile test-file}}))))
      ;; Mock save-file function to throw an error 
      (with-redefs-fn {#'lunch.file-util/save-file (fn [t1 t2] (throw (IOException. "Fails")))
                       #'lunch.models.menu/exists? (fn [id] false)
                       #'lunch.models.menu/insert! (fn [params conn] ())}
        #(is (thrown+? application-exception? (menu-upload {:id "123" :file {:tempfile test-file}})))))

    (testing "menu-upload"
      (is (thrown+? application-exception? (menu-download nil)))
      ;; Returns empty when not found in db
      (with-redefs-fn {#'lunch.models.menu/find-by-id (fn [id] ())}
        #(is (response? 404 (menu-download "whatever"))))
      (with-redefs-fn {#'lunch.models.menu/find-by-id (constantly "/a/file") 
                       #'res/file-response (constantly file-response)}
        #(is (response? 200 (menu-download "12345")))
      ))))




