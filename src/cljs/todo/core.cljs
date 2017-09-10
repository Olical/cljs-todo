(ns todo.core
  (:require [reagent.core :as reagent]
            [bonsai.core :as bonsai]))

(defn root [state!]
  [:p "Hello, World!"])

(defn init! []
  (reagent/render [root (reagent/atom {})] (.getElementById js/document "app")))
