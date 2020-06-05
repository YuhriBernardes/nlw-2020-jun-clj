(ns nlw.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.route.definition.table :as route-table]
            [ring.util.response :as ring-res]
            [io.pedestal.http.cors :as cors]))

(def hello
  {:name ::hello
   :enter (fn [context]
            (assoc context :responde (ring-res/response "Hello World")))})

(def app-routes (route-table/table-routes #{["/hello" :get hello]}))
(def url-for (route/url-for-routes app-routes))

(defn stop-server! []
  (swap! *server* http/stop))

(defn add-dev-config [m]
  (-> m
      (http/dev-interceptors)
      (assoc ::http/join? false)))

(defn add-prod-config [m]
  (-> m
      (assoc ::http/join? true)
      (assoc ::http/host "0.0.0.0")
      (assoc ::http/port 80)
      (assoc ::http/allowed-origins (cors/allow-origin {:creds true :allowed-origins (constantly true)}))))

(defn map->server
  ([m] (map->server m :dev))
  ([m profile]
   (let [base-config (-> m
                         (http/default-interceptors))
         full-config (cond-> base-config
                       (= profile :dev)  (add-dev-config)
                       (= profile :prod) (add-prod-config))]
     (http/create-server full-config))))

(defn -main [& _]
  (doto "Hello World!"
    prn))
