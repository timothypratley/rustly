(ns rustly.main
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [rustly.compile :as compiler]))

(def cli-options
  [["-i" "--in FILENAME" "Input file"
    :missing "Input file not specified"
    :validate [(fn check-filename [filename]
                 (.exists (io/file filename)))
               "File must exist"]]
   ["-o" "--out FILENAME" "Output file"
    :default "out.rs"]
   ["-v" "--verbose" "Reports progress"]
   ["-h" "--help" "Print usage information"]])

(defn -main [& args]
  (let [{:keys [errors summary options]} (cli/parse-opts args cli-options :strict true)]
    (cond
      errors (do (println (str/join \newline errors))
                 (System/exit -1))
      (:help options) (do (println "Options:")
                          (println summary))
      :else
      (let [{:keys [verbose]} options]
        (when verbose
          (println "Options" (pr-str options)))
        (compiler/compile options)
        (when verbose
          (println "Done"))))))
