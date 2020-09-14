(ns caliper.img
  (:require [clojure.string :as str]))


;; responsive images content/storage

(def standard-widths [200 400 800 1600 3200])

(defn actual-widths [picture-width]
      (into [picture-width]
            (filter #(< % picture-width) standard-widths)))

(defn src [picture-host slug width]
      (str "https://" picture-host "/scaled/" width "w/" slug))

(defn srcset [picture-host slug picture-width actual-widths]
      (str/join ","
                (map
                  (fn [width]
                      (let [picture-url (if (= picture-width width)
                                          (str "https://" picture-host "/" slug)
                                          (src picture-host slug width))]
                           (str picture-url " " width "w")))
                  actual-widths)))

(defn default-width [actual-widths requested-default-width picture-width]
      (or (reduce (fn [_ width]
                      (if (= width requested-default-width)
                        (reduced width)))
                  nil
                  actual-widths)
          picture-width))

(defn img [& {:keys [alt picture default-intrinsic-width sizes class style]}]
      (let [{:keys [:picture/slug :picture/width :picture/host]} picture
            actual-widths (actual-widths width)]
           [:img (merge {:alt     alt
                         :srcset  (srcset host slug width actual-widths)
                         :src     (src host slug (default-width actual-widths default-intrinsic-width width))
                         :sizes   sizes
                         :class   class
                         :loading :lazy}
                        (if style {:style style}))]))
