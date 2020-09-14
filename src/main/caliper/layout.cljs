(ns caliper.layout
  (:require [caliper.css :as css :refer-macros [defclass]]
            [garden.selectors :as sel])
  (:require-macros [caliper.layout :refer [defstack]]))

;; Utilities

(defn embed-in-li [element]
  [:li element])

(def embed-in-li-xform (map embed-in-li))

(defn cond-comp [f g]
  (if f
    (comp f g)
    g))

;; Layout components as functions and macros

;; 1. Stack

(defn gen-stack-style [& {:keys [direction
                                 fixed-gap
                                 gap-var default-gap
                                 root-style-options]}]
  (let [flex-direction (if (= direction :horizontal) :row :column)
        gap-margin     (if (= direction :horizontal) :margin-left :margin-top)
        non-gap-margin (if (= direction :horizontal) :margin-right :margin-bottom)]
    [(merge root-style-options
            {:display-flex   :flex
             :flex-direction flex-direction}
            (if gap-var {gap-var (or default-gap 0)}))
     [(css/direct-child :*) {gap-margin 0 non-gap-margin 0}]
     [(css/direct-child (sel/+ :* :*)) {gap-margin (if gap-var (css/cssvar gap-var) fixed-gap)}]]))


(defstack v-stack
  :direction :vertical)

(defstack h-stack
  :direction :horizontal
  :root-style-options {:align-items :flex-start})

;; Sidebar

;; Uses a artificial div for the gap
(defn gen-sidebar-style [& {:keys [gap-half-length
                                   root-style-options
                                   left-side-options
                                   right-side-options]}]
  [[(css/direct-child :*) (merge root-style-options
                                 {:display   :flex
                                  :flex-wrap :wrap}
                                 (if-not (= gap-half-length 0)
                                   {:margin (str "-" gap-half-length)}))]
   [(css/direct-child (sel/> :* :*)) {:margin gap-half-length}]
   [(css/direct-child (sel/> :* sel/first-child)) left-side-options]
   [(css/direct-child (sel/> :* (sel/nth-child 2))) right-side-options]])

;; Navigation Bar

;(defn-and-class horizontal-list [& children]
;                [{:padding-right 0
;                  :padding-left  0
;                  :display       :flex}
;                 [(sel/li (sel/not sel/first-child) (sel/before))] {:content       "\"|\""
;                                                                    :padding-right "0.5rem"
;                                                                    :padding-left  "0.5rem"}]
;                [:ul {calssasihtoenasiht}])

(defclass -horizontal-list
  {:padding-left                0
   :padding-right               0
   :display                     :flex
   :list-style                  :none
   :--horizontal-list-separator ""
   :--horizontal-list-half-gap  0}
  [(sel/li (sel/not sel/first-child) (sel/before))
   {:content (css/cssvar :--horizontal-list-separator)}
   {:padding-left (css/cssvar :--horizontal-list-half-gap)}
   {:padding-right (css/cssvar :--horizontal-list-half-gap)}])

(defn horizontal-list [& {:keys [children xform separator half-gap class attributes]}]
  (into
    [:ul (merge attributes
                {:class (into [-horizontal-list] class)}
                (if (or separator half-gap)
                  {:style (merge
                            (if separator {:--horizontal-list-separator separator})
                            (if half-gap {:--horizontal-list-half-gap half-gap}))}))]
    (cond-comp xform embed-in-li-xform)
    children))