{:jig/components

 {
  :mqtt-decoder
  {:jig/component jig.netty.mqtt/MqttDecoder
   :jig/project "../mqtt-broker/project.clj"}

  :mqtt-encoder
  {:jig/component jig.netty.mqtt/MqttEncoder
   :jig/project "../mqtt-broker/project.clj"}

  :mqtt-handler
  {:jig/component mqtt-broker.broker/MqttHandler
   :jig/project "../mqtt-broker/project.clj"}

  :mqtt-server
  {:jig/component jig.netty/Server
   :jig/dependencies [:mqtt-decoder :mqtt-encoder :mqtt-handler]
   :jig/project "../mqtt-broker/project.clj"
   :port 1883}

  :cljs-builder
  {:jig/component jig.cljs-builder/Builder
   :jig/project "../mqtt-broker/project.clj"
   :output-dir "../mqtt-broker/target/js"
   :output-to "../mqtt-broker/target/js/main.js"
   :source-map "../mqtt-broker/target/js/main.js.map"
   :optimizations :none
   }

  :cljs-server
  {:jig/component jig.bidi/ClojureScriptRouter
   :jig/dependencies [:cljs-builder]
   :jig.web/context "/cljsjs/"
   }

  :admin
  {:jig/component mqtt-broker.admin/Website
   :jig/project "../mqtt-broker/project.clj"
   :jig/dependencies []
   }

  :admin/webevents
  {:jig/component mqtt-broker.admin/Webevents
   :jig/project "../mqtt-broker/project.clj"
   :jig/dependencies []
   }

  :admin/routing
  {:jig/component jig.bidi/Router
   :jig/project "../mqtt-broker/project.clj"
   :jig/dependencies [:admin/webevents :cljs-server :admin]
   ;; Optionally, route systems can be mounted on a sub-context
   ;;:jig.web/context "/services"
   }

  :admin/server
  {:jig/component jig.http-kit/Server
   :jig/project "../mqtt-broker/project.clj"
   :jig/dependencies [:admin/routing]
   :port 8000}

  }}
