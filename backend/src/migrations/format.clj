(ns migrations.format
  (:require [clojure.string :as str]
            [honeysql-postgres.util :as pg-util]
            [honeysql.format :as sql-fmt]))

(defmethod sql-fmt/fn-handler "varchar" [_ char-count]
  (str "VARCHAR(" char-count ")" ))

(defmethod sql-fmt/fn-handler "required" [& _]
  "NOT NULL")

(defmethod sql-fmt/fn-handler "foreign" [_ table column]
  (let [table-normalized (sql-fmt/to-sql table)
        coumn-normalized (sql-fmt/to-sql column)]
    (str "REFERENCES " table-normalized "(" coumn-normalized ")")))

(defmethod sql-fmt/format-clause :order-by-desc [[_ fields] _]
  (str "ORDER BY "
       (sql-fmt/comma-join (for [field fields]
                     (if (sequential? field)
                       (let [[field & modifiers] field]
                         (str/join " "
                                      (cons (sql-fmt/to-sql field)
                                            (for [modifier modifiers]
                                              (case modifier
                                                :desc        "DESC"
                                                :asc         "ASC"
                                                :nulls-first "NULLS FIRST"
                                                :nulls-last  "NULLS LAST"
                                                "")))))
                       (sql-fmt/to-sql field)))) " DESC"))

(defmethod sql-fmt/format-clause :create-schema [[_ [schema-name if-not-exists]] _]
  (str "CREATE SCHEMA "
       (when if-not-exists "IF NOT EXISTS ")
       (-> schema-name
           pg-util/get-first
           sql-fmt/to-sql)))

(defmethod sql-fmt/format-clause :drop-schema [[_ [schema-name if-exists]] _]
  (str "DROP SCHEMA "
       (when if-exists "IF EXISTS ")
       (-> schema-name
           pg-util/get-first
           sql-fmt/to-sql)))
