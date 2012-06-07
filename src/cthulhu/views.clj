(ns cthulhu.views
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page-helpers :only [html5 include-css include-js]]
        [hiccup.form-helpers]
        [noir.fetch.remotes :only [defremote]])
  (:require [noir.response :as response]
            [cljsbinding.core :as binding]
            [monger.collection :as monger])
  (:import [org.bson.types ObjectId]))

(defpage "/" []
  (html5
    [:head
      [:title "My Little Cthulhu"]
      (include-css "/screen.css")]
    [:body
      [:h1
        [:img {:src "/hellocthulhu-right.png"}]
        "My Little Cthulhu"
        [:img {:src "/hellocthulhu-left.png"}]]
      [:ul
        [:li  {:bindseq "cthulhu.client.goos"
               :bind "class: cthulhu.client.classname"}
          [:span.check {:bind "text: cthulhu.client.check;click: cthulhu.client.toggle"}]
          [:span {:bind "text: cthulhu.client.title"}]]]
      (form-to [:post "/new"] (text-field "goo"))
      (include-js "jquery.js")
      (include-js "client.js")
      (binding/bind)]))

(defn unidify [doc]
  (assoc doc :_id (str (:_id doc))))
(defn idify [doc]
  (assoc doc :_id (ObjectId. (:_id doc))))

(defn get-goos []
  (map unidify (monger/find-maps "goo")))

(defremote goo-list []
  (get-goos))

(defremote save-goo [goo]
  (monger/save "goo" (idify goo))
  (get-goos))

(defpage [:post "/new"] {:keys [goo]}
  (monger/insert "goo" {:goo goo :done false})
  (response/redirect "/"))

