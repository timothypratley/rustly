(ns rustly.compile
  (:refer-clojure :exclude [compile])
  (:require [clojure.java.io :as io]
            [fipp.clojure :as pprint]
            [rustly.parse :as parser]
            [rustly.rust-emit :as e]
            [rustly.rust-translate :as t]))

(defn compile [{:keys [in out verbose]}]
  (when verbose
    (println "Reading..."))
  (let [forms (parser/read-file in)
        _ (when verbose
            (println "Normalizing..."))
        normalized-forms (map t/translate forms)]
    (when verbose
      (doseq [form normalized-forms]
        (pprint/pprint form)))
    (when verbose
      (println "Writing..."))
    (with-open [w (io/writer out)]
      (.write w "use rpds::{List, Vector, HashTrieMap, HashTrieSet};\n")
      (doseq [form normalized-forms]
        (.write w ^String (e/emit form))))))
