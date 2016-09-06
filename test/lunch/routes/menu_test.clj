(ns lunch.routes.menu-test
  (:import (java.io IOException File))
  (:require [lunch.routes.menu :refer :all]
            [lunch.models.menu :as menu-model]
            [lunch.exceptions :refer :all]
            [ring.mock.request :as mock]
            [lunch.file-util :as futil]
            [slingshot.test]
            [clojure.test :refer :all]))

(defn response? [code res]
  (= (:status res) code))

(def test-file (File. "/tmp"))

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
                       #'lunch.models.menu/insert! (fn [params] ())}
        #(is (response? 201 (menu-upload {:id "123" :file {:tempfile test-file}}))))
      ;; Mock save-file function to throw an error 
      (with-redefs-fn {#'lunch.file-util/save-file (fn [t1 t2] (throw (IOException. "Fails")))
                       #'lunch.models.menu/exists? (fn [id] false)
                       #'lunch.models.menu/insert! (fn [params] ())}
        #(is (thrown+? application-exception? (menu-upload {:id "123" :file {:tempfile test-file}})))))

    (testing "menu-upload"
      (is (thrown+? application-exception? (menu-download nil)))
      (with-redefs-fn { #'lunch.models.menu/find-by-id (fn [id] ())}
        #(is (response? 404 (menu-download "whatever"))))
      )))




