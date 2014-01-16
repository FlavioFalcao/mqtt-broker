;; Copyright © 2014, OpenSensors.IO. All Rights Reserved.
(ns mqtt-broker.admin
  (:require
   [clojure.java.io :as io]
   jig
   [jig.bidi :refer (add-bidi-routes)]
   [bidi.bidi :refer (->Redirect ->Resources)])
  (:import
   (jig Lifecycle)))

(defn index [req]
  {:status 200 :body (slurp (io/resource "public/index.html"))})

(defn make-routes []
  ["/"
   [["index.html" index]
    ["" (->Redirect 307 index)]
    ["" (->Resources {:prefix "public/"})]]])

(deftype Website [config]
  Lifecycle
  (init [_ system] system)
  (start [_ system]
    (-> system (add-bidi-routes config (make-routes))))
  (stop [_ system] system))
