(ns bugsbio.squirrel.yesql-test
  (:require
    [bugsbio.squirrel.yesql :as yes]
    [expectations           :refer :all]
    [clojure.java.io        :as io]))

(expect (more-> fn? #(-> % :inserts :cat)
                fn? #(-> % :updates :cat)
                fn? #(-> % :queries :cats-for-name))
        (yes/query-map))
