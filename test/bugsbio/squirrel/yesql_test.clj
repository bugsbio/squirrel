(ns bugsbio.squirrel.yesql-test
  (:require
    [bugsbio.squirrel.test-db :as test-db]
    [bugsbio.squirrel.yesql   :refer [defquery defupdate] :as y]
    [environ.core             :refer [env]]
    [expectations             :refer :all]))

(defquery  get-cats    "sql/get_cats.sql"     {:connection test-db/db-spec :serializers {}})
(defupdate create-cat  "sql/create_cat.sql"   {:return :row})
(defupdate update-cat  "sql/update_cat.sql"   {:return :row})
(defupdate delete-cats "sql/delete_cats.sql"  {:return :count})

;; If you have already deleted all the cats, there will be no cats to delete
(expect 0
        (do (delete-cats {})
            (delete-cats {})))

;; If there are cats to be deleted, you'll get a count of ones you delete.
(expect 1
        (do (delete-cats {})
            (create-cat {:name "Fred" :favourite-food "Tuna"})
            (delete-cats {})))

;; Queries work
(expect [{:name "Fred" :favourite-food "Tuna"}]
        (do (delete-cats {})
            (create-cat {:name "Fred" :favourite-food "Tuna"})
            (get-cats {:favourite-food "Tuna"})))

;; If you delete all the cats, then there are no cats
(expect []
        (do (delete-cats {})
            (create-cat {:name "Fred" :favourite-food "Tuna"})
            (get-cats {:favourite-food "Quorn"})))

;; You can update the cats, and get the updated rows back
(expect {:name  "Anne", :favourite-food  "Salmon"}
        (do (delete-cats {})
            (create-cat {:name "Lisa" :favourite-food "Salmon"})
            (create-cat {:name "Jenny" :favourite-food "Salmon"})
            (update-cat {:name "Anne" :favourite-food "Salmon"})))

(run-tests [*ns*])
