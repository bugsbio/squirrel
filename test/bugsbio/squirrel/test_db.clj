(ns bugsbio.squirrel.test-db
  (:require
    [environ.core :refer [env]]))

(def db-spec
  {:user (env :db-user "postgres")
   :password (env :db-password "")
   :subname (str "//" (env :db-host "localhost") ":" (env :db-port 5433) "/" (env :db-db "squirrel"))
   :subprotocol "postgresql"
   :classname "org.postgresql.Driver"})

(defn in-context
  "rebind a var, expecations are run in the defined context"
  {:expectations-options :in-context}
  [work]
  (with-redefs [bugsbio.squirrel.yesql/*conn* {:connection db-spec}] (work)))
