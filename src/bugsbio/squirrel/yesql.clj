(ns bugsbio.squirrel.yesql
  (:require
    [yesql.core                 :refer [defquery]]
    [clojure.string             :as s]
    [clojure.java.io            :as io]
    [com.stuartsierra.component :as component])
  (:import [java.io File]))


(defn extension
  [f]
  (last (s/split (.getName f) #"\.")))

(defn relative-path
  [d f]
  (s/replace-first (.getPath f) (str (.getPath d) "/") ""))

(defn sql-file?
  [f]
  (and (.isFile f) (= "sql" (extension f))))

(defn path-tokens
  [relative-path]
  (-> relative-path
      (s/replace #".sql$" "")
      (s/replace #"_" "-")
      (s/split (re-pattern File/separator))
      (->> (map keyword))))

(defn resource-path
  [relative-path]
  (str "sql" File/separator relative-path))

(defn make-query
  [query-type resource-path]
  (-> (case query-type
        :queries
        (defquery temp-query   resource-path)
        :updates
        (defquery temp-update! resource-path)
        :inserts
        (defquery temp-insert<! resource-path))
      (first)
      (deref)))

(defn sql-files
  [directory]
  (filter sql-file? (file-seq directory)))

(defn query-map
  "Build a map of query functions from source sql files in a SQL directory
  on your resource path."
  []
  (let [directory (io/file (io/resource "sql"))]
    (reduce (fn [acc file]
              (let [relative-path (relative-path directory file)
                    resource-path (resource-path relative-path)
                    path-tokens   (path-tokens relative-path)
                    query-type    (first path-tokens)
                    query         (make-query query-type resource-path)]
                (assoc-in acc path-tokens query)))
            {} (sql-files directory))))

(defrecord YesDB [connection]
  component/Lifecycle
  (start [this]
    (assoc this :query-map (query-map)))
  (start [this]
    (dissoc this :query-map)))
