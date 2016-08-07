(ns lunch.api.place
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [cljs-http.client :as http]
            [lunch.api.handlers :refer [wrap-get-handler wrap-post-handler]]
            [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(defn place [id] (str "api/place/" id))

(defn get-one
  [db]
  {:url (place (get-in db [:view :place-id]))})

(defn upload-place
  [db file]
  {:url (str (place (get-in db [:view :place-id])) "/upload")
   :params {:multipart-params [["file" file]]}})

(re-frame/register-handler
 :api-place/get-one
 (wrap-get-handler get-one))

(re-frame/register-handler
 :api-place/upload-place
 (wrap-post-handler upload-place))
