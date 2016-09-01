(ns lunch.handler-test
  (:import java.io.IOException)
  (:require [lunch.handler :refer :all]
            [lunch.exceptions :refer :all]
            [ring.mock.request :as mock]
            [lunch.file-util :as futil]
            [slingshot.test]
            [clojure.test :refer :all]))

(defn response? [code res]
  (= (:status res) code))

(deftest test-place-upload
  ;; Test input validation
  (is (thrown+? application-exception? (place-upload nil)))
  (is (thrown+? application-exception? (place-upload {:id 123 :file {:tempfile "/tmp/afile"}})))
  (is (thrown+? application-exception? (place-upload {:id nil :file {:tempfile "/tmp/afile"}})))
  (is (thrown+? application-exception? (place-upload {:id "123" :file {:tempfile nil}})))
  ;; Mock save-file function to return a path
  (with-redefs-fn {#'lunch.file-util/save-file (fn [t1 t2] (str "/bin/lib/123.pdf"))}
    #(is (response? 200 (place-upload {:id "123" :file {:tempfile "/var/something"}}))))
  ;; Mock save-file function to throw an error 
  (with-redefs-fn {#'lunch.file-util/save-file (fn [t1 t2] (throw (IOException. "Fails")))}
    #(is (thrown+? application-exception? (place-upload {:id "123" :file {:tempfile "/var/something"}}))))
  )
