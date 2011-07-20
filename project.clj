(defproject clj-quickrepl "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0-beta1"]
                 [org.clojure/data.json "0.1.0"]
                 [org.clojure/tools.logging "0.1.2"]
                 [org.clojure/core.incubator "0.1.0"]
                 
                 [clj-http "0.1.3"]
                 [com.ashafa/clutch "0.2.4"]

                 [joda-time "1.6.2"]
                 [log4j/log4j "[1.2,)" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jdmk/jmxtools
                                                    com.sun.jmx/jmxri]]])

