(defproject squirrel "0.1.3-yesql-SNAPSHOT"
  :description "Utility functions for translating between Clojure and SQL idioms."
  :url "http://github.com/bugsbio/squirrel"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :signing {:gpg-key "CF73E6ED"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [cheshire "5.3.1"]]
  :profiles {:dev {:dependencies [[expectations "2.0.9"]
                                  [environ "1.0.0"]
                                  [org.postgresql/postgresql "9.4-1201-jdbc41"]]
                   :plugins [[lein-autoexpect "1.0"]
                             [lein-expectations "0.0.8"]]
                   :resource-paths ["test/resources"]}
             :provided {:dependencies [[yesql "0.5.0-rc1"]]}})
