(ns lunch.specs
  (:require [clojure.spec :as s]
            [clojure.string :refer [blank?]]
            ))

(s/def ::id (s/and string? (complement blank?)))
