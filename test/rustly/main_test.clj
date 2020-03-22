(ns rustly.main-test
  (:require [clojure.test :refer :all]
            [rustly.main :as main]))

(deftest -main-test
  (testing "Command line invocation"
    (is (nil? (main/-main "--in" "example/hello_world/hello_world.clj")))))
