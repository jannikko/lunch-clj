(ns lunch.handler-test
  (:require [lunch.handler :refer [catch-all-handler]]
            [lunch.exceptions :refer [ApplicationException]]
            [slingshot.slingshot :refer [throw+]]
            [clojure.test :refer :all]))

(def mock-request {})

(defn throw-assertion-err [request] (throw (AssertionError. "Validation failed")))
(defn throw-exception [request] (throw (Exception. "Critical exception")))
(defn throw-application-error [request] (throw+ (ApplicationException 407 "Application Exception")))

(deftest handler
  (testing "handler"
    (testing "catch-all-handler"
      (is (= {:status 400 :headers {} :body nil} ((catch-all-handler throw-assertion-err) mock-request)))
      (is (= {:status 500 :headers {} :body nil} ((catch-all-handler throw-exception) mock-request)))
      (is (= {:status 407 :headers {} :body "Application Exception"} ((catch-all-handler throw-application-error) mock-request))))))
