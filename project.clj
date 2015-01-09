(defproject squirrel "0.1.2-SNAPSHOT"
  :description "Utility functions for translating between Clojure and SQL idioms."
  :url "http://github.com/bugsbio/squirrel"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :signing {:gpg-key "CF73E6ED"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.stuartsierra/component "0.2.2"]
                 [cheshire "5.3.1"]
                 [yesql "0.5.0-rc1"]]
  :profiles {:dev {:dependencies [[expectations "2.0.9"]]
                   :plugins [[lein-autoexpect "1.0"]
                             [lein-expectations "0.0.8"]]}})
