(ns caliper.xform)

(defmacro ensure-xform [xform]
  `(or ~xform (map identity)))