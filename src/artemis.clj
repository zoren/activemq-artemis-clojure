(ns user
  (:import
    [org.apache.activemq.artemis.api.core.client
    ActiveMQClient]
    [org.apache.activemq.artemis.api.core
    QueueConfiguration
    TransportConfiguration]
    [org.apache.activemq.artemis.core.remoting.impl.invm InVMConnectorFactory]
    [org.apache.activemq.artemis.core.config.impl ConfigurationImpl]
    [org.apache.activemq.artemis.core.server.embedded EmbeddedActiveMQ]
    ))

; stolen from https://activemq.apache.org/components/artemis/documentation/latest/embedding-activemq.html

(def config
  (doto
    (ConfigurationImpl.)
    (.addAcceptorConfiguration "in-vm" "vm://0")
    (.addAcceptorConfiguration "tcp" "tcp://127.0.0.1:61616")
    (.setSecurityEnabled false)
    (.setPersistenceEnabled false)
    ))

(def server
    (doto (EmbeddedActiveMQ.)
      (.setConfiguration config)))

(.start server)

(def server-locator (ActiveMQClient/createServerLocator "vm://0"))

(def session-factory (.createSessionFactory server-locator))

(def session (.createSession session-factory))
;(def session (.createSession session-factory "ss" "pass" false false false false 10))
(def address "address-example")
(def queue "queue-example")
(.createQueue session address queue true)

(def producer (.createProducer session address))

(def message (.createMessage session true))
(.writeString (.getBodyBuffer message) "hello")

(.send producer message)
(.start session)
(def consumer (.createConsumer session queue))

(def received-message (.receive consumer))

(.readString (.getBodyBuffer received-message))
