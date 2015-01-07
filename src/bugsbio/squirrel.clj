(ns bugsbio.squirrel
  (:require
    [clojure.string :as s]
    [clojure.edn    :as edn]
    [cheshire.core  :as json]))

(defprotocol Serializer
  (serialize
    [this v]
    "'Serialize' the given value into a form suitable for use in SQL.")
  (deserialize
    [this v]
    "'Deserialize' the given value into the form required by Clojure."))

;; Default implementation if no serializer is provided
(extend-protocol Serializer
  nil
  (serialize   [this v] v)
  (deserialize [this v] v))

(def ^{:doc "Serializer than renders maps as JSON strings"}
  json
  (reify Serializer
    (serialize   [this v] (json/generate-string v))
    (deserialize [this v] (json/parse-string v true))))

(def ^{:doc "Serializer that renders ratios as strings"}
  edn
  (reify Serializer
    (serialize [this v]   (pr-str v))
    (deserialize [this v] (edn/read-string v))))

(defn map-keys
  "Map a function over the keys of a map, returning a map."
  [f m]
  (into {} (for [[k v] m] [(f k) v])))

(defn to-snake-case
  "Converts a kebab-case key to its snake-case equivalent."
  [k]
  (keyword (s/replace (name k) #"-" "_")))

(defn to-kebab-case
  "Converts a snake-case key to its kebab-case equivalent."
  [k]
  (keyword (s/replace (name k) #"_" "-")))

(defn- deserialize-all
  [serializers m]
  (reduce (fn [acc [k s]]
            (cond (map? s)
                  (update-in acc [k] (partial deserialize-all s))
                  :else
                  (update-in acc [k] (partial deserialize s))))
          m serializers))

(defn- serialize-kv
  [serializers [k v]]
  [k (serialize (get serializers k) v)])

(defn- get-nested-keys
  [k]
  (map keyword (s/split (name k) #"--")))

(defn- nest
  "Interprets double underscores in keys to mean an element of a nested map,
  where the left hand of the underscore is the key of the map and the right
  hand is the key in the nested map."
  [m]
  (reduce (fn [acc [k v]]
            (assoc-in acc (get-nested-keys k) v)) {} m))

(defn to-sql
  "Converts the kebab-case keys of a map into their snake-case equivalent.
  Can be passed an optional map of clojure-key -> serializer which will be used
  to serialize the identified fields into a form suitable for use in SQL."
  ([m]
   (to-sql m {}))
  ([m serializers]
   (->> (map (partial serialize-kv serializers) m)
        (map-keys to-snake-case))))

(defn to-clj
  "Converts the snake-case keys of a map into their kebab-case equivalent.
  Can be passed an optional map of clojure-key -> deserializer which will be used
  to deserialize the identified fields into a form required by your Clojure code.

  Keys containing double underscores are interpreted as nested maps,
  where the left hand of the underscore is the key of the map and the right
  hand is the key in the nested map."
  ([m]
   (to-clj m {}))
  ([m serializers]
   (->> (map-keys to-kebab-case m)
        (nest)
        (deserialize-all serializers))))
