(ns lunch.shared-specs
  (:require [clojure.spec :as s]
            [clojure.string :refer [blank?]]
            ))

(s/def ::place-id (s/and string? (complement blank?)))
