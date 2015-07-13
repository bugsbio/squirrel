(ns bugsbio.squirrel.yesql
  (:require
    [bugsbio.squirrel :as sq]
    [yesql.core       :as yes]))

(def ^:dynamic *conn* nil)

(defn build-conn
  [arg opts]
  (merge arg *conn* (select-keys opts [:connection :result-set-fn :row-fn])))

(defmacro defquery
  [name file & [opts]]
  `(let [name#     (gensym)
         query-fn# (first (yes/defquery name# ~file))]
     (defn ~name
       [data# & [conn#]]
       (-> data#
           (sq/to-sql)
           (query-fn# (build-conn conn# ~opts))
           (->> (map sq/to-clj))))))

(defn- update-suffix
  [{:keys [return] :or {return :count}}]
  (case return
    :count "!"
    :row   "<!"))

(defmacro defupdate
  [name file & [opts]]
  (let [name* (symbol (str (gensym) (update-suffix opts)))]
  `(let [query-fn# (first (yes/defquery ~name* ~file))]
     (defn ~name
       [data# & [conn#]]
       (-> data#
           (sq/to-sql)
           (query-fn# (build-conn conn# ~opts))
           (cond->>
             (= :row (:return ~opts)) sq/to-clj))))))
