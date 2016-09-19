(ns lunch.specs
  (:require [clojure.spec :as s]
            [clojure.string :refer [blank?]]))

(s/def ::non-blank-string (s/and string? (complement blank?)))
