(ns caliper.css
  (:require
    [clojure.string :as str]
    [garden.selectors :as sel]
    [garden.types :as gt]))

(defn css-custom-property? [k]
  (and (keyword? k)
       (str/starts-with? (name k) "--")))

(defn class-name [symbol]
  (str (str/replace (str *ns* "--" symbol) "." "-")))

(defn class-ident [class-name]
  (str "." class-name))

(defn direct-child [a]
  (sel/selector (str "> " (sel/css-selector a))))

(defn cssvar [& args]
  #?(:clj  (gt/->CSSFunction "var" args)
     :cljs (gt/CSSFunction. "var" args)))

(defmulti css-for identity)

#?(:clj
   (defmacro register-class [class-name styles]
     `(let [class-name#  ~class-name
            class-ident# (class-ident class-name#)
            styles#      ~styles]
        (defmethod css-for class-name# [~'_]
          (into [class-ident#] styles#)))))

#?(:clj
   (defmacro defclass [ident & body]
     (let [class-name# (class-name ident)]
       `(do
          (register-class ~class-name# [~@body])
          (def ~ident ~class-name#)))))