;; Copyright © 2014, OpenSensors.IO. All Rights Reserved.
(ns mqtt-broker.admin
  (:require
   [clojure.java.io :as io]
   jig
   [jig.bidi :refer (add-bidi-routes)]
   [org.httpkit.server :refer (with-channel websocket? on-receive send! on-close)]
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

(defn create-receive-handler [ch]
  (fn [data]
    (println "Data received on channel:" data)))

(def websocket-connections (atom []))

(deref websocket-connections)

(deftype Webevents [config]
  Lifecycle
  (init [_ system] system)
  (start [_ system]
    (println "Starting web socket listener!")
    (-> system (add-bidi-routes config
                                ["/events"
                                 (fn [req]
                                   (with-channel req channel
                                     (when (websocket? channel)
                                       (println "websocket connecting")
                                       (swap! websocket-connections conj channel)
                                       (send! channel "Hello!")
                                       (on-receive channel (create-receive-handler channel))
                                       (on-close channel (fn [status] (swap! websocket-connections #(remove #{channel}  %)))))))])))
  (stop [_ system] system))
