(defproject squirrel "0.1.0"
  :description "Utility functions for translating between Clojure and SQL idioms."
  :url "http://github.com/bugsbio/squirrel"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :signing {:gpg-key "CF73E6ED"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [cheshire "5.3.1"]]
  :profiles {:dev {:dependencies [[expectations "2.0.9"]]
                   :plugins [[lein-autoexpect "1.0"]
                             [lein-expectations "0.0.8"]]}})
