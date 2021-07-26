(ns app.client.ui.headlessui
  "CLJS Fulcro translation of headlessui https://headlessui.dev/"
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.dom-common :refer [interpret-classes]]
            ["@headlessui/react" :refer [Dialog
                                         Disclosure
                                         FocusTrap
                                         Listbox
                                         Menu
                                         Popover
                                         Portal
                                         RadioGroup
                                         Switch
                                         Transition]]))

(def components
  {:dialog Dialog
   :dialog-description (.-Description Dialog)
   :dialog-overlay (.-Overlay Dialog)
   :dialog-title (.-Title Dialog)
   :disclosure Disclosure
   :disclosure-button (.-Button Disclosure)
   :disclosure-panel (.-Panel Disclosure)
   :focus-trap FocusTrap
   :listbox Listbox
   :listbox-button (.-Button Listbox)
   :listbox-label (.-Label Listbox)
   :listbox-option (.-Option Listbox)
   :listbox-options (.-Options Listbox)
   :menu Menu
   :menu-button (.-Button Menu)
   :menu-item (.-Item Menu)
   :menu-items (.-Items Menu)
   :popover Popover
   :popover-button (.-Button Popover)
   :popover-group (.-Group Popover)
   :popover-overlay (.-Overlay Popover)
   :popover-panel (.-Panel Popover)
   :portal Portal
   :portal-group (.-Group Portal)
   :radio-group RadioGroup
   :radio-group-description (.-Description RadioGroup)
   :radio-group-label (.-Label RadioGroup)
   :radio-group-option (.-Option RadioGroup)
   :switch Switch
   :switch-description (.-Description Switch)
   :switch-group (.-Group Switch)
   :switch-label (.-Label Switch)
   :transition Transition
   :transition-child (.-Child Transition)})

(defn headless-ui
  ([component-name props]
   (headless-ui component-name props nil))
  ([component-name props children]
   (let [component (get components component-name)
         component (interop/react-factory component)
         props (interpret-classes props)]
     (component props children))))
