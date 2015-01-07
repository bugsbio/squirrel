# squirrel

:squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel::squirrel:

Utility functions for translating between Clojure and SQL idioms. Add `[squirrel "0.1.1"]` to the dependencies vector of you project.clj.

The two you need to know are `to-sql` and `to-clj`:

## Converting clojure maps to be used in SQL queries / updates

`to-sql` maps the keys of a map from Clojure style `:kebab-case` to SQL style `:snake_case`, ready for use by things like clojure.java.jdbc or yeqsl.

```clojure
(require '[bugsbio.squirrel :as sq])
(sq/to-sql {:a-nice-cat 777 :some-lovely-cheese "Yes, please."})
;; => {:a_nice_cat 777 :some_lovely_cheese "Yes, please."}
```

It also accepts an optional map of serializers that will be applied to these fields:

```clojure
(sq/to-sql {:my-json-field {:a 1 :b 2}} {:my-json-field sq/json})
;; => {:my_json_field "{\"a\":1,\"b\":2}"}
```

The keys of the serializer map should match the Clojure style key names.

## Converting maps returned from SQL to Clojure style ones

`to-clj` maps the keys of a map from SQL style `:snake_case` to Clojure style `:kebab-case`.

```clojure
(sq/to-clj {:a_nice_cat 777 :some_lovely_cheese "Yes, please."})
;; => {:a-nice-cat 777 :some-lovely-cheese "Yes, please."}
```

It also accepts an optional map of serializers - these will be used to *deserialize* their respective fields.

```clojure
(sq/to-clj {:my_json_field "{\"a\":1,\"b\":2}"})
;; => {:my-json-field {:a 1 :b 2}}
```


A final nicety of `to-clj` is that it interprets double underscores in keys as indicating nested maps.
So a single-level map returned by a SQL query can be converted into a nice nested map for use in Clojure.

```clojure
(sq/to-clj {:a 1 :b__c 2 :b__d 3 :b__e__f 4 :b__e_g "Spot the difference"})
;; => {:a 1 :b {:c 2 :d 3 :e {:f 4} :e-g "Spot the difference"}}
```


## Serializers

Serializers are just anything that satisfies the `bugsbio.squirrel.Serializer` protocol, which requires just two functions: `serialize` and `deserialize`.
As they can do both, you can use the same map of serializers with both `to-sql` and `to-clj`.

Squirrel provides two serializers out of the box: `bugsbio.squirrel/json` and `bugsbio.squirrel/edn`.

Here's how you could define your own:

```clojure
(def keyword
  (reify sq/Serializer
    (serialize   [this v] (name v))
    (deserialize [this v] (clojure.core/keyword v))))
```

## Changelog

* 0.1.1 - fix bug where keys in nested maps weren't converted to kebab-case
