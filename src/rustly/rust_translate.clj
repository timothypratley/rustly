(ns rustly.rust-translate
  (:require [clojure.string :as str]
            [meander.epsilon :as m]
            [meander.strategy.epsilon :as s]))

(declare translate)

(defn chain [[x [c & chains]]]
  (if c
    (recur [(list* '. x c) chains])
    x))

(defn ends-with-dot? [s]
  (str/ends-with? s "."))

(defn drop-trailing-dot [s]
  (symbol (str/replace s #"\.$" "")))

(defn starts-with-dot? [s]
  (str/starts-with? s "."))

(defn drop-leading-dot [s]
  (symbol (str/replace s #"^\." "")))

;; thought: Types map, methods map (conj->insert)
;; pros: easy
;; cons: might not provide as rich expression in the target language


(def translate
  "Main entry point for transpilation.
  Given a Clojure form, normalizes it to rust equivalents."
  (s/rewrite
    (let [!x !y ..?n] . !body ..?m)
    (let [!x (m/app translate !y) ..?n] . (m/app translate !body) ..?m)

    (ns & ?body)
    (ns & ?body)

    (fn ?name ?args . !body ...)
    (fn ?name ?args . (m/app translate !body) ...)

    ;; constructor shorthand
    ((m/pred ends-with-dot? ?t) & ?args)
    (new ~(drop-trailing-dot ?t) & ~(map translate ?args))

    ;; method shorthand
    ((m/pred starts-with-dot? ?m) ?obj & ?args)
    ('. ?obj ~(drop-leading-dot ?m) & ?args)

    ;; '(1 2 3)
    ('quote . !members ...)
    (m/app chain [(new List) ((push_front (m/app translate !members)) ...)])

    (!x ...)
    ((m/app translate !x) ...)

    ;; [1 2 3]
    [!members ...]
    (m/app chain [(new Vector) ((push_back (m/app translate !members)) ...)])

    ;; {:a 1, :b 2}
    {& (m/seqable [!ks !vs] ...)}
    (m/app chain [(new HashTrieMap) ((insert (m/app translate !ks) (m/app translate !vs)) ...)])

    ;; #{:a :b :c}
    #{^& (m/seqable !vs ...)}
    (m/app chain [(new HashTrieSet) ((insert (m/app translate !vs)) ...)])

    ?else ?else))
