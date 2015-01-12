(ns bugsbio.squirrel.util
  (:require
    [clojure.string :as s]))

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

(defn- get-nested-keys
  [k]
  (map keyword (s/split (name k) #"--")))

(defn nest-on-double-dash
  "Interprets double dashes in keys to mean an element of a nested map,
  where the left hand of the underscore is the key of the map and the right
  hand is the key in the nested map."
  [m]
  (reduce (fn [acc [k v]]
            (assoc-in acc (get-nested-keys k) v)) {} m))


