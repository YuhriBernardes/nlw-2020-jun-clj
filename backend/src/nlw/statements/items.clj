(ns nlw.statements.items
  (:require [honeysql.core :as sql]
            [honeysql.format]
            [honeysql.helpers :as sql-helper]
            [next.jdbc :as jdbc]))

(def ^:private point-table :nlw.t-collect-point)
(def ^:private item-table :nlw.t-collection-item)
(def ^:private point-item-table :nlw.t-point-item)

(defn fetch-all
  ([data-source]
   (let [execute! (partial jdbc/execute! data-source)]
     (-> (sql-helper/select :*)
         (sql-helper/from item-table)
         sql/format
         execute!)))
  ([data-source queries]
   (let [execute! (partial jdbc/execute! data-source)]
     (-> (sql-helper/select :*)
         (sql-helper/from item-table)
         (sql-helper/where queries)
         sql/format
         execute!))))

(defn save! [data-source & entities]
  (let [execute! (partial jdbc/execute! data-source)]
    (-> (sql-helper/insert-into item-table)
        (sql-helper/values entities)
        sql/format
        execute!)))


(defn fetch-by-id [data-source id]
  (let [execute! (partial jdbc/execute! data-source)]
    (some-> (sql-helper/select :*)
            (sql-helper/from item-table)
            (sql-helper/where [:= :id id])
            sql/format
            execute!
            first)))
