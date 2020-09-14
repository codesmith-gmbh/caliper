(ns caliper.mount
  (:require ["react-dom" :as rdom]))

(defn mount! [element-id element]
  (rdom/render
    element
    (js/document.getElementById (name element-id))))
