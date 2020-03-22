(ns hello-world
  (:require [ferris_says :refer [say]]
            [std.io :refer [stdout BufWriter]]))

(fn main []
  (let [stdout (stdout)
        out "Hello world"
        _l (quote 1 2 3)
        _v [1 2 3]
        _m {"a" "b" "c" "d"}
        _s #{1 2 3}
        ;; This type hint is optional
        ^:i64 width 24
        ^:mut writer (BufWriter. (.lock stdout))]
    (.unwrap (say out width ^:mut writer))))
