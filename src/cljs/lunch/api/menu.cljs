(ns lunch.api.menu
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [lunch.api.handlers :refer [wrap-get-handler wrap-post-handler]]
            [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(defn menu [id] (str "api/menu/" id))

(defn get-one
  [db]
  {:url (str (menu (get-in db [:view :place-id])) "/download")})

(defn upload-menu
  [db link]
  {:url    (str (menu (get-in db [:view :place-id])) "/upload")
   :params {:json-params {"link" link}}})

(re-frame/register-handler
 :api-menu/get-one
 (wrap-get-handler get-one))

(re-frame/register-handler
 :api-menu/upload-menu
 (wrap-post-handler upload-menu))
