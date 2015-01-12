(ns bugsbio.squirrel.util-test
  (:require
    [bugsbio.squirrel.util :as u]
    [expectations          :refer :all]))

;; map-keys maps a function over the keys of a map, returing a map
(expect {:a 1 :b 2 :c 3}
        (u/map-keys keyword {"a" 1 "b" 2 "c" 3}))

;; to-snake-case converts kebab-case keys to their snake-case equivalent.
(expect :cat_vomiting_rainbows
        (u/to-snake-case :cat-vomiting-rainbows))

;; to-kebab-case converts kebab-case keys to their kebab-case equivalent.
(expect :cat-vomiting-rainbows
        (u/to-kebab-case :cat_vomiting_rainbows))

;; nest-on__ interprets double underscores in keys to mean an element of a nested map,
;; where the left hand of the underscore is the key of the map and the right
;; hand is the key in the nested map."
(expect {:a {:b {:c 1}}}
        (u/nest-on-double-dash {:a--b--c 1}))
