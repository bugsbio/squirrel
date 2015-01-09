(ns bugsbio.squirrel.yesql-test
  (:require
    [bugsbio.squirrel.yesql :as yes]
    [expectations           :refer :all]
    [clojure.java.io        :as io]))

(def dir  (io/file (io/resource "sql")))
(def file (io/file (io/resource "sql/queries/cats_for_name.sql")))

(expect "sql"
        (yes/extension file))

(expect "queries/cats_for_name.sql"
        (yes/relative-path dir file))

(expect true
        (yes/sql-file? file))

(expect false
        (yes/sql-file? (io/file ".")))

(expect [:queries :cats-for-name]
        (-> (yes/relative-path dir file)
            (yes/path-tokens)))

(expect "sql/queries/cats_for_name.sql"
        (yes/resource-path (yes/relative-path dir file)))

(expect fn?
        (->> (yes/relative-path dir file)
             (yes/resource-path)
             (yes/make-query :queries)))

(expect ["inserts/cat.sql"
         "queries/cats_for_name.sql"
         "updates/cat.sql"]
        (map (partial yes/relative-path dir)
             (yes/sql-files dir)))

(expect (more-> fn? #(-> % :inserts :cat)
                fn? #(-> % :updates :cat)
                fn? #(-> % :queries :cats-for-name))
        (yes/query-map))
