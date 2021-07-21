(ns app.client.ui.icons
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            ["@heroicons/react/outline" :refer [InformationCircleIcon
                                                TrashIcon]]
            ["@heroicons/react/solid" :refer [PencilAltIcon]]))

(def icons
  {:info InformationCircleIcon
   :edit PencilAltIcon
   :delete TrashIcon})

(defn ui-icon
  ([icon-name]
   (ui-icon icon-name nil))
  ([icon-name className]
   (let [icon (get icons icon-name)
         icon (interop/react-factory icon)
         className (or className "w-5 h-5 stroke-current")]
     (icon {:className className}))))
