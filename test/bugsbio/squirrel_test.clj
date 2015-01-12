(ns bugsbio.squirrel-test
  (:require
    [bugsbio.squirrel :as s]
    [expectations     :refer :all]))

;; to-sql accepts an optional map of keys to serializers
(expect {:happy_dog 14 :the_fish "{:is-this-bad \"no\"}"}
        (s/to-sql {:happy-dog 14 :the-fish {:is-this-bad "no"}}
                  {:the-fish s/edn}))

;; to-clojure converts a map's kebab-case keys to snake case
(expect {:happy-dog 14}
        (s/to-clj {:happy_dog 14}))

;; to-clj accepts an optional map of keys to serializers
(expect {:happy-dog 14 :the-fish {:is-this-bad "no"}}
        (s/to-clj {:happy_dog 14 :the_fish "{:is-this-bad \"no\"}"}
                  {:the-fish s/edn}))

;; to-clj interprets any keys with double underscores as nested maps
(expect {:happy-dog 6 :ecstatic-cat {:seven "things" :nine "coffee"}}
        (s/to-clj {:happy_dog 6
                   :ecstatic_cat__seven "things"
                   :ecstatic_cat__nine "coffee"}))

;; keys in submaps are converted to kebab case, too
(expect {:poop {:faeces {:waste-matter "cameron"}}}
        (s/to-clj {:poop__faeces__waste_matter "cameron"}))

;; Maps can be arbitrarily nested, and so can serializer functions
(expect {:a {:b {:c {:happy-dog "joy! joy!"}}}}
        (s/to-clj {:a__b__c "{\"happy-dog\":\"joy! joy!\"}"}
                  {:a {:b {:c s/json}}}))
