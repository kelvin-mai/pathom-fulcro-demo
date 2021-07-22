(ns app.client.ui.forms
  (:require [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.dom :as dom]
            [app.client.ui.headlessui :refer [headless-ui]]
            [app.client.ui.icons :refer [ui-icon]]))

(def input-style :.border.rounded.w-full.py-2.px-3.text-gray-700.leading-tight.focus:outline-none.focus:shadow-outline)
(def label-style :.text-gray-800.text-sm.font-bold.capitalize)

(defn field-attrs [component field]
  (let [form         (comp/props component)
        entity-ident (comp/get-ident component form)
        id           (str (first entity-ident) "-" (second entity-ident))
        is-dirty?    (fs/dirty? form field)
        clean?       (not is-dirty?)
        validity     (fs/get-spec-validity form field)
        is-invalid?  (= :invalid validity)
        value        (get form field "")]
    {:dirty?   is-dirty?
     :ident    entity-ident
     :id       id
     :clean?   clean?
     :validity validity
     :invalid? is-invalid?
     :value    value}))

(defn ui-input [component field {:keys [label] :as props}]
  (let [{:keys [id value invalid?]} (field-attrs component field)
        classes (cond-> [input-style]
                  (:classes props) (concat (:classes props))
                  (:className props) (conj (:className props))
                  invalid? (conj "border-red"))
        props (merge
               (dissoc props :classes :className :label)
               {:id id
                :value value
                :classes classes
                :onBlur #(comp/transact! component [(fs/mark-complete! {:field field})])})]
    (dom/div
     (dom/label {:htmlFor id
                 :classes [label-style]}
                label)
     (dom/input props))))

(defn ui-dropdown [{:keys [value onChange options placeholder] :as props}]
  (headless-ui
   :listbox
   (dissoc props :options :placeholder)
   (fn [inner-props]
     (dom/div
      {:classes ["relative mt-1 text-gray-600 capitalize"]}
      (headless-ui
       :listbox-button
       {:className "w-full text-left relative overflow-hiden py-2 pl-3 pr-12 bg-white hover:bg-gray-50 border shadow rounded-md outline-none cursor-default"}
       (comp/fragment
        (dom/span
         :.ml-3.truncate
         {:classes [(when (not value) "text-gray-400 normal-case")]}
         (if value
           (:label (first (filter #(= value (:value %)) options)))
           placeholder))
        (dom/span
         :.ml-3.absolute.inset-y-0.right-0.pr-2.pointer-events-none.flex.items-center
         (if (.-open inner-props)
           (ui-icon :chevron-up "w-5 h-5 stroke-current")
           (ui-icon :chevron-down "w-5 h-5 stroke-current")))))
      (headless-ui
       :transition
       {:enter "transition-opacity duration-75"
        :enterFrom "opacity-0"
        :enterTo "opacity-100"
        :leave "transition-opacity duration-150"
        :leaveFrom "opacity-100"
        :leaveTo "opacity-0"}
       (headless-ui
        :listbox-options
        {:className "absolute w-full top-full max-h-48 bg-white border shadow-md rounded-md mt-1 z-auto overflow-y-auto"}
        (map
         (fn [option]
           (headless-ui
            :listbox-option
            {:key (str (:value option) "_" (:label option))
             :className "cursor-pointer block px-3 py-2 hover:bg-gray-50"
             :classes [(when (= value (:value option)) "bg-gray-100")
                       (when (nil? (:value option)) "text-gray-400 normal-case")]
             :value (:value option)}
            (:label option)))
         options)))))))
