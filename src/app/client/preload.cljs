(ns app.client.preload
  (:require
    [com.fulcrologic.fulcro.algorithms.timbre-support :as ts]
    [taoensso.timbre :as log]))

(js/console.log "[DEV] logging set to :debug")
(log/set-level! :debug)
(log/merge-config! {:output-fn ts/prefix-output-fn
                    :appenders {:console (ts/console-appender)}})
