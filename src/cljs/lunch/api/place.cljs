(ns lunch.api.place
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [lunch.util :refer [GET]]
            [lunch.api.endpoints :refer [place]]
            [cljs-http.client :as http]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(defn get-one
  [id]
  (GET (place id)))
