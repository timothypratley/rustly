(ns rustly.core
  (:require [clojure.java.io :as io]
            [clojure.tools.cli :as cli]
            [clojure.edn :as edn]
            [rustly.compiler :as compiler]))

(def cli-options
  [["-i" "--in FILENAME" "Input file"
    :default "example.clj"
    :validate [(fn check-filename [filename]
                 (.exists (io/file filename)))
               "Output file must exist"]]
   ["-o" "--out FILENAME" "Output file"
    :default "out.rs"]
   ["-v" "--verbosity LEVEL" "Verbosity level"
    :default 0
    :validate [number? "Must be a number"]
    :parse-fn edn/read-string
    :update-fn inc]
   ["-h" "--help"]])

(defn -main [& args]
  (println "Welcome to rustly")
  (let [options (cli/parse-opts args cli-options)]
    (if (:help options)
      (println cli-options)
      (compiler/compile options)))
  (println "Done"))
