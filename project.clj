(def jig-version "2.0.0-RC7-SNAPSHOT")

(defproject opensensors/mqtt-broker "0.1.0"
  :description "The OpenSensors.io MQTT broker"
  :url "https://github.com/OpenSensorsIO/mqtt-broker"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]

                 ;; Jig extensions
                 [jig/protocols ~jig-version]
                 [jig/async ~jig-version]
                 [jig/netty ~jig-version]
                 [jig/netty-mqtt ~jig-version]
                 [jig/cljs-builder ~jig-version]
                 [jig/http-kit ~jig-version]
                 [jig/bidi ~jig-version]

                 ;; ClojureScript dependencies
                 [cljs-ajax "0.2.3"]
                 [om "0.1.7"]
                 ]

  :source-paths ["src" "src-cljs"]
)
