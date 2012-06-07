(ns cthulhu.server
  (:use cthulhu.helpers)
  (:require [noir.server :as server]
            [cthulhu.views]))

(defn -main [& args]
  (monger-connect! "mongodb://127.0.0.1/cthulhu")
  (server/start 1337))
