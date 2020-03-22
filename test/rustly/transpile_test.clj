(ns rustly.transpile-test
  (:require [clojure.test :refer [deftest is testing]]
            [rustly.rust-translate :as t]))

(deftest chain-test
  (is (= '(. (. (. (new List) push_back 1) push_back 2) push_back 3)
         (t/chain ['(new List) '((push_back 1)
                                 (push_back 2)
                                 (push_back 3))]))))
