;; Copyright © 2014, OpenSensors.IO. All Rights Reserved.
(ns mqtt-broker.broker
  (:require jig)
  (:import
   (io.netty.channel ChannelHandlerAdapter)
   (jig Lifecycle)))

(def registered-topics
  {"/uk/gov/hackney/parking" #{"yodit" "paula"}
   "/juxt/big-red-button/malcolm" #{"malcolm"}
   "/juxt/big-red-button/yodit" #{"yodit"}})

(def valid-users #{["yodit" "letmein"]
                   ["michael" "clojurewerkz!"]
                   ["malcolm" "password"]
                   ["paula" "123"]})

(defn authenticated? [{:keys [username password]}]
  (nil? (true? (valid-users [username password]))))

(defn make-channel-handler [{:keys [subs connections]}]
  (proxy [ChannelHandlerAdapter] []
    (channelRead [ctx msg]
      (case (:type msg)

        :connect
        (do
          (println "Got connection!" (pr-str ctx) (pr-str msg))
          ;; TODO Validate connection protocol name and assert version is 3.1
          (dosync (alter connections assoc ctx (assoc msg :authenticated? (authenticated? msg))))
          (.writeAndFlush ctx {:type :connack}))

        :subscribe (do (.writeAndFlush ctx {:type :suback})
                       (dosync
                        (alter subs (fn [subs]
                                      (reduce #(update-in %1 [%2] conj ctx)
                                              subs (map first (:topics msg)))))))
        :publish
        (do
          (println "Got message!" (pr-str ctx) (pr-str msg))
          (let [conn (get @connections ctx)]
            (when conn
              (println "Sending response to conn: " conn)
              (doseq [ctx (get @subs (:topic msg))]
                (.writeAndFlush ctx msg)))))

        :pingreq (.writeAndFlush ctx {:type :pingresp})

        :disconnect (.close ctx)))
    (exceptionCaught [ctx cause]
      (try (throw cause)
           (finally (.close ctx))))))

(deftype MqttHandler [config]
  Lifecycle
  (init [_ system] system)
  (start [_ system]
    (assoc-in system
              [(:jig/id config) :jig.netty/handler-factory]
              #(make-channel-handler {:subs (ref {})
                                      :connections (ref {})})))
  (stop [_ system] system))
