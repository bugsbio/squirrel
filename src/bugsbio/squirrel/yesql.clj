(ns bugsbio.squirrel.yesql
  (:require
    [bugsbio.squirrel            :as sq]
    [bugsbio.squirrel.yesql.util :as u]
    [yesql.core                  :refer [defquery]]
    [clojure.java.io             :as io]
    [com.stuartsierra.component  :as component])
  (:import [java.io File]))

(defn query-map
  "Build a map of query functions from source sql files in a SQL directory
  on your resource path."
  []
  (let [directory (io/file (io/resource "sql"))]
    (reduce (fn [acc file]
              (let [relative-path (u/relative-path directory file)
                    resource-path (u/resource-path relative-path)
                    path-tokens   (u/path-tokens relative-path)
                    query-type    (first path-tokens)
                    query         (u/make-query query-type resource-path)]
                (assoc-in acc path-tokens query)))
            {} (u/sql-files directory))))

(defrecord YesDB [connection-pool]
  component/Lifecycle
  (start [this] (assoc  this :query-map (query-map)))
  (stop  [this] (dissoc this :query-map)))

(defn new-yes-db
  "Create a new YesDB requires a 'connection' dependency
  that will provide a database connection or connection pool."
  []
  (map->YesDB {}))

(defn- run-query
  [data db query-type query-id opts]
  (let [query-id    (if (seq? query-id) query-id (vector query-id))
        yesql-fn    (-> db :query-map query-type (get-in query-id))
        connection  (merge (:connection db) opts)]
    (yesql-fn data connection)))

(defn update!
  [data db query-id & [opts]]
  (run-query data db :updates query-id opts))

(defn query
  [data db query-id & [opts]]
  (run-query data db :queries query-id opts))

(defn insert!
  [data db query-id & [opts]]
  (run-query data db :inserts query-id opts))
