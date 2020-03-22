(ns rustly.parse
  (:require [clojure.java.io :as io]
            [clojure.tools.reader.edn :as r]
            [clojure.tools.reader.reader-types :as rt]))

(def EOF (Object.))
(def to-eof {:eof-error? false :eof EOF})

(defn read-file [filename]
  (with-open [rr (rt/input-stream-push-back-reader (io/input-stream filename))
              reader (rt/source-logging-push-back-reader rr 1 filename)]
    (doall
     (take-while
      #(not (= % EOF))
      (repeatedly #(r/read to-eof reader))))))
