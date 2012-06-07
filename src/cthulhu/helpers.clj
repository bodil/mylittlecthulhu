(ns cthulhu.helpers
  (:use [clojure.test :only [with-test is]]
        [clojure.string :only [split]])
  (:require [monger core]))

(with-test
    (defn- parse-mongodb-uri
      "Turn a mongodb:// URL into a map of the form { :host :port :db :auth { :user :pass }"
      [url]
      (let [uri (bean (java.net.URI. url))]
        {:host (:host uri)
         :port (let [port (:port uri)]
                 (if (< port 0) 27017 port))
         :db ((split (:path uri) #"/") 1)
         :auth (let [userinfo (:userInfo uri)]
                 (if (nil? userinfo) nil
                     (let [items (split userinfo #":")
                           user (items 0)
                           pass (items 1)]
                       { :user user :pass pass})))}))
  (is (= { :host "localhost" :port 27017 :db "ohai" :auth nil}
         (parse-mongodb-uri "mongodb://localhost/ohai")))
  (is (= { :host "caturday.me" :port 31337 :db "cheezburger" :auth nil}
         (parse-mongodb-uri "mongodb://caturday.me:31337/cheezburger")))
  (is (= { :host "localhost" :port 27017 :db "ohai"
          :auth {:user "foo" :pass "bar"}}
         (parse-mongodb-uri "mongodb://foo:bar@localhost/ohai"))))

(with-test
    (defn- uri-to-loggable
      "Render a MongoDB URI map as a string suitable for logging, ie. with password removed."
      [uri]
      (let [{:keys [host port db auth]} uri
            auth-str (if auth (str (:user auth) "@") "")
            port-str (if (= 27017 port) "" (str ":" port))]
        (str "mongodb://" auth-str host port-str "/" db)
        ))
  (is (= "mongodb://longcat@caturday.me:31337/ohai"
         (uri-to-loggable
          (parse-mongodb-uri "mongodb://longcat:secret@caturday.me:31337/ohai"))))
  (is (= "mongodb://caturday.me/ohai"
         (uri-to-loggable
          (parse-mongodb-uri "mongodb://caturday.me/ohai")))))

(defn monger-connect!
  "Connect to a MongoDB database given a mongodb:// style URL."
  [url]
  (let [uri (parse-mongodb-uri url)]
    (monger.core/connect! uri)
    (let [auth (:auth uri)]
      (when auth
       (monger.core/authenticate (:db uri) (:user auth) (char-array (:pass auth)))))
    (monger.core/set-db! (monger.core/get-db (:db uri)))
    (println "Connected to MongoDB at" (uri-to-loggable uri))))

