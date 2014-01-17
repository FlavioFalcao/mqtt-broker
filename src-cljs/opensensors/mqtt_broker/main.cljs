;; Copyright © 2014, OpenSensors.IO. All Rights Reserved.
(ns opensensors.mqtt-broker.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [cljs.core.async :refer [<! chan put! sliding-buffer]]
   [ajax.core :refer (GET POST)]))

(.log js/console "ClojureScript ready!")

(let [ws (js/WebSocket. "ws://localhost:8000/events")]
  (set! (.-onmessage ws)
        (fn [ev]
          (let [message (.-data ev)]
            (.log js/console "OMG, a message!" message)))))
