(ns rustly.compiler
  (:refer-clojure :exclude [compile])
  (:require [clojure.java.io :as io]
            [rustly.parser :as parser]
            [rustly.transpiler :as transpiler]))

(defn compile [{{:keys [in out verbose] :as opts} :options}]
  (prn 'OPTIONS opts)
  (when verbose
    (println "Reading..."))
  (let [forms (parser/read-file in)]
    (when verbose
      (println "Compiling..."))
    (with-open [w (io/writer out)]
      (.write w "use rpds::{List, Vector, HashTrieMap, HashTrieSet};\n")
      (doseq [form forms]
        (.write w ^String (transpiler/translate form))))))
