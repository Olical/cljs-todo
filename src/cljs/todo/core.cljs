(ns todo.core
  (:require [reagent.core :as reagent]
            [bonsai.core :as bonsai]))

;; Alright, I'll need to get data and actions correct first.
;; Then wire things up to those actions with some rendering.
;; Stuff you can do:
;;  * Create a todo.
;;  * Mark todos as done.
;;  * Mark all todos as done.
;;  * Delete a todo.
;;  * Delete all done.
;;  * Filter todos.

;; Actions.

(defn create-todo
  "Creates a todo and appends it to the list."
  [state content]
  (update state :todos conj {:done false
                             :content content}))

(defn toggle-todo-done
  "Toggles the done state of the todo at the given index."
  [state index]
  (update-in state [:todos index :done] not))

(defn toggle-all-todos-done
  "Toggles all todo done states. If everything is done, it marks them as not
  done. If some of them aren't done, it marks them all as done."
  [state]
  (update state :todos
          (fn [todos]
            (let [desired-state (not (every? :done todos))]
              (vec (map #(assoc % :done desired-state) todos))))))

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
            (vec (remove :done todos)))))

(defn change-filter-mode
  "Changes the filter mode to the given keyword."
  [state filter-mode]
  (assoc state :filter-mode filter-mode))

;; Queries.

(defn filtered-todos
  "Get the todos filtered by :filter-mode."
  [{:keys [filter-mode todos]}]
  (case filter-mode
    :all todos
    :active (remove :done todos)
    :completed (filter :done todos)))

;; State.

(defn initial-state []
  {:todos []
   :filter-mode :all})

;; Views.

(defn root
  "The root component for the application, binds everything together."
  [state!]
  [:p "..."])

(defn init!
  "Entry point for the application, mounts the root into the DOM."
  []
  (reagent/render [root (reagent/atom (initial-state))] (.getElementById js/document "app")))
