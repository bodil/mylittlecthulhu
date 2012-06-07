(ns cthulhu.client
  (:use-macros [crate.def-macros :only [defpartial]]
               [fetch.macros :only [remote]])
  (:require [crate.core :as crate]
            [fetch.remotes :as remotes]
            [cljsbinding :as binding]))

(def goos (atom []))

(defn ^:export title [item] (:goo item))
(defn ^:export classname [item] (if (:done item) "done" "open"))
(defn ^:export check [item] (if (:done item) "\u2611" "\u2610"))

(defn ^:export toggle [item]
  (fn []
    (let [item (assoc item :done (not (:done item)))]
      (remote (save-goo item) [new-goos]
        (reset! goos new-goos)))))

(remote (goo-list) [new-goos]
  (reset! goos new-goos))

(.log js/console "hello")

