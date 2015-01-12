(ns bugsbio.squirrel
  (:require
    [bugsbio.squirrel.serializers :as s]
    [bugsbio.squirrel.util        :as u]))

(defn to-sql
  "Converts the kebab-case keys of a map into their snake-case equivalent.
  Can be passed an optional map of clojure-key -> serializer which will be used
  to serialize the identified fields into a form suitable for use in SQL."
  ([m]
   (to-sql m {}))
  ([m serializers]
   (when m
     (->> (s/serialize-all serializers m)
          (u/map-keys u/to-snake-case)))))

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
   (when m
     (->> (u/map-keys u/to-kebab-case m)
          (u/nest-on-double-dash)
          (s/deserialize-all serializers)))))

(def ^{:doc "Serializer that renders Clojure values as EDN strings"}
  edn
  s/edn)

(def ^{:doc "Serializer than renders maps as JSON strings"}
  json
  s/json)
