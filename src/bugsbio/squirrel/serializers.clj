(ns bugsbio.squirrel.serializers
  (:require
    [clojure.edn           :as edn]
    [cheshire.core         :as json]))

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


(defn deserialize-all
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

(defn serialize-all
  [serializers m]
  (map (partial serialize-kv serializers) m))

(def ^{:doc "Serializer than renders maps as JSON strings"}
  json
  (reify Serializer
    (serialize   [this v] (json/generate-string v))
    (deserialize [this v] (json/parse-string v true))))

(def ^{:doc "Serializer that renders Clojure values as EDN strings"}
  edn
  (reify Serializer
    (serialize [this v]   (pr-str v))
    (deserialize [this v] (edn/read-string v))))
