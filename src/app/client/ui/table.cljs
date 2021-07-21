(ns app.client.ui.table
  (:require [com.fulcrologic.fulcro.dom :as dom]))

(def table-row :.border-b.border-gray-200.bg-white.tr)

(def table-cell :.py-3.px-6.text-left.truncate)

(defn ui-table-head [head]
  (dom/th :.py-3.px-6.text-left
          {:key head}
          head))

(defn ui-table [{:keys [heads classes]} children]
  (dom/div :.bg-white.shadow-md.rounded-md
           {:classes classes}
           (dom/table :.w-full.table-fixed
                      (dom/thead
                       (dom/tr :.bg-gray-200.uppercase.text-sm
                               (map
                                ui-table-head
                                heads)))
                      (dom/tbody :.text-gray-800.text-sm
                                 children))))
