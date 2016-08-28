(ns lunch.handler-test
  (:require [lunch.handler :refer :all]
            [lunch.exceptions :refer :all]
            [ring.mock.request :as mock]
            [slingshot.test]
            [clojure.test :refer :all]))


(deftest test-place-upload
  (is (thrown+? application-exception? (place-upload nil))))
