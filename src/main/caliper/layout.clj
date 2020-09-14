(ns caliper.layout
  (:require [caliper.css :as css]
            [caliper.xform :as xform]))

(defmacro defstack [ident & {:keys [direction
                                    fixed-gap default-gap
                                    root-style-options]}]
  (let [css-class-name# (css/class-name ident)
        gap-var-name#   (str "--" ident "-gap")]
    `(let [direction#          ~direction
           fixed-gap#          ~fixed-gap
           gap-var#            (if (nil? fixed-gap#)
                                 ~gap-var-name#)
           default-gap#        ~default-gap
           root-style-options# ~root-style-options]
       (css/register-class ~css-class-name#
                           (gen-stack-style :direction direction#
                                            :fixed-gap fixed-gap#
                                            :gap-var gap-var# :default-gap default-gap#
                                            :root-style-options root-style-options#))
       (defn ~ident [& {:keys [~'class ~'children ~'xform ~'gap]}]
         (into [:div
                (merge {:class (into [~css-class-name#] ~'class)}
                       (if ~'gap {:style {~gap-var-name# ~'gap}}))]
               (xform/ensure-xform ~'xform)
               ~'children)))))