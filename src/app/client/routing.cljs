(ns app.client.routing
  "Modified from https://github.com/dvingo/my-clj-utils/blob/master/src/main/dv/fulcro_reitit.cljs
  (provided under an MIT license)"
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as c :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.algorithms.normalized-state :refer [swap!->]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [goog.object :as g]
   [reitit.core :as r]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]
   [taoensso.timbre :as log]))

(defn router-state* [app]
  (-> app ::app/state-atom deref (get-in [::router :router])))

(defn router-registered? [app]
  (boolean (router-state* app)))

(defn map-vals [f m]
  (into {} (map (juxt key (comp f val))) m))

(defn make-routes-by-name
  "Returns a map like: {:root {:name :root :path '/'}}"
  [router]
  (let [grouped (group-by (comp :name second) router)]
    (map-vals
      ;; takes the path string and adds it as the key :path
     (fn [[[path-str prop-map]]]
       (assoc prop-map :path path-str))
     grouped)))

(defn fulcro->reitit-route [t]
  (let [component (c/component-name t)
        opts (c/component-options t)
        route (get opts ::route)
        f (first route)]
    (when-not route (log/error (str "no route on" component)))
    (cond
      (string? f)
      [route]

      (vector? f)
      route)))

(defn fulcro->reitit-routes [fulcro-router]
  (let [router-targets (dr/get-targets fulcro-router)]
    (reduce
     (fn [acc target]
       (into acc (fulcro->reitit-route target)))
     []
     router-targets)))

(defn register-routes! [app routes]
  (let [{::app/keys [state-atom]} app]
    (swap! state-atom
           (fn [s]
             (assoc-in s [::router :router] {:reitit-router (rf/router routes)
                                             :routes-by-name (make-routes-by-name routes)
                                             :current-fulcro-route []})))))

(defn set-router-state! [app k v]
  (swap! (::app/state-atom app)
         (fn [s] (assoc-in s [::router :router k] v))))

(defn on-match [app match]
  (log/info "on-match " match)
  (let [{:keys [reitit-router]} (router-state* app)
        {:keys [path] :as m} (or match {:path (g/get js/location "pathname")})
        has-match? (rf/match-by-path reitit-router path)]
    (if has-match?
      (let [segment (-> m :data :segment)]
        (set-router-state! app :current-fulcro-route segment)
        (dr/change-route! app segment))

      (do
        (log/info "no route match, redirecting to default")
        (js/setTimeout (rfe/push-state :default))))))

(defn start-router! [app fulcro-router]
  (log/info "starting router")
  (dr/initialize! app)
  (register-routes! app (fulcro->reitit-routes fulcro-router))
  (rfe/start!
   (rf/router (fulcro->reitit-routes fulcro-router))
   (partial on-match app)
   {:use-fragment false}))
