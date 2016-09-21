(ns lunch.views.session)

(defn render-entries
  [session]
  (let [session-entries (:session-entries session)]
    (->> session-entries
         (into [])
         (map (fn [[row-num entry]] (assoc entry :row-num row-num))))))

(defn render-metadata
  [session]
  {:place-id (:place-id session)})
