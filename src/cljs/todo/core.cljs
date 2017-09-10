(ns todo.core
  (:require [reagent.core :as reagent]
            [bonsai.core :as bonsai]))

;; Actions.

(defn create-todo
  "Creates a todo from the draft content and appends it to the list."
  [{:keys [draft-todo-content] :as state}]
  (if (empty? draft-todo-content)
    state
    (-> state
        (update :todos conj {:done? false
                             :content draft-todo-content})
        (assoc :draft-todo-content ""))))

(defn toggle-todo-done
  "Toggles the done state of the todo at the given index."
  [state index]
  (update-in state [:todos index :done?] not))

(defn toggle-all-todos-done
  "Toggles all todo done states. If everything is done, it marks them as not
  done. If some of them aren't done, it marks them all as done."
  [state]
  (update state :todos
          (fn [todos]
            (let [desired-state (not (every? :done? todos))]
              (vec (map #(assoc % :done? desired-state) todos))))))

(defn delete-todo
  "Delete the todo at the given index."
  [state index]
  (update state :todos
          (fn [todos]
            (vec (concat (subvec todos 0 index) (subvec todos (inc index)))))))

(defn delete-all-done-todos
  "Deletes all done todos."
  [state]
  (update state :todos
          (fn [todos]
            (vec (remove :done? todos)))))

(defn change-filter-mode
  "Changes the filter mode to the given keyword."
  [state filter-mode]
  (assoc state :filter-mode filter-mode))

(defn change-draft-todo
  "Changes the content of the currently editing todo."
  [state draft-content]
  (assoc state :draft-todo-content draft-content))

;; Queries.

(defn filtered-todos
  "Get the todos filtered by :filter-mode."
  [{:keys [filter-mode todos]}]
  (case filter-mode
    :all todos
    :active (remove :done? todos)
    :completed (filter :done? todos)))

;; State.

(defn initial-state []
  {:todos []
   :filter-mode :all
   :draft-todo-content ""})

(defonce state! (reagent/atom (initial-state)))

;; Views.

(defn todo-editor
  "Allows you to create todos."
  []
  (let [{:keys [draft-todo-content]} @state!]
    [:input {:type "text"
             :value draft-todo-content
             :on-key-press #(when (= 13 (-> % .-charCode))
                              (bonsai/next! state! create-todo))
             :on-change #(bonsai/next! state! change-draft-todo (-> % .-target .-value))}]))

(defn todo-list
  "Renders the list of todos."
  []
  (let [state @state!
        todos (filtered-todos state)]
    [:ul
     (doall
      (map-indexed
       (fn [index {:keys [done? content]}]
         [:li
          {:key index}
          [:input {:type "checkbox"
                   :checked done?
                   :on-change #(bonsai/next! state! toggle-todo-done index)}]
          content
          [:button {:on-click #(bonsai/next! state! delete-todo index)}
           "delete"]])
       todos))]))

(defn root
  "The root component for the application, binds everything together."
  []
  [:div
   [:div
    [:button {:on-click #(bonsai/next! state! toggle-all-todos-done)}
     "toggle all"]
    [:button {:on-click #(bonsai/next! state! delete-all-done-todos)}
     "delete done"]]
   [todo-editor]
   [todo-list]])

(defn init!
  "Entry point for the application, mounts the root into the DOM."
  []
  (reagent/render [root] (.getElementById js/document "app")))
