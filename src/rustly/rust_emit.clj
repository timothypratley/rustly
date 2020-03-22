(ns rustly.rust-emit
  (:require [clojure.string :as str]
            [meander.epsilon :as m]
            [meander.strategy.epsilon :as s]))

(declare emit)

(defn args-list [args]
  (str "(" (str/join ", " (map emit args)) ")"))

(defn construct [c args]
  (str c "::new" (args-list args)))

(defn method [object method args]
  (str (emit object) "." method (args-list args)))

(defn statement [form]
  (str (emit form) ";\n"))

(defn maybe-mutable-ref [x]
  (str (when (:mut (meta x)) "&mut ")
       x))

(def require-clause
  "[foo.bar :refer [baz]] => use foo::bar{baz};"
  (s/match
    [?library :refer [!symbols ...]]
    (str "use " (str/replace ?library "." "::")
         "::{" (str/join ", " !symbols) "};\n")
    ?else
    (throw (ex-info "FAIL" {:input ?else}))))

(def translate-require
  "The Clojure ns form is notoriously nuanced.
  For now only a frequently used subset is covered."
  (s/match
    (:require . !clause ...)
    (str/join (map require-clause !clause))
    ?else
    (throw (ex-info "FAIL" {:input ?else}))))

(defn annotate-let [x]
  (let [{:keys [mut tag]} (meta x)]
    (str (when mut "mut ")
         x
         (when tag (str ": " tag)))))

(def emit-list
  "Most interesting Clojure expressions are lists"
  (s/match
    ;; (new Type)
    (new ?class & ?args)
    (construct ?class ?args)

    ;; (. obj method)
    ('. ?obj ?method & ?args)
    (method ?obj ?method ?args)

    ;; (ns foo.bar (:require [baz :as b]))
    (ns ?name . !imports ...)
    (str/join (map translate-require !imports))

    ;; TODO: implicit do...
    ;; (fn foo [arg] (inc arg))
    (fn ?name ?args & ?body)
    (str "fn " ?name (args-list ?args) " {" \newline
         (str/join (map statement ?body))
         "}" \newline)

    ;; (let [x 1])
    (let [!xs !ys ...] & ?body)
    (str/join
       (concat
         (map
           (partial format "let %s = %s;\n")
           (map annotate-let !xs)
           (map emit !ys))
         (map emit ?body)))

    ;; TODO: implicit do...
    (do & ?body)
    (str/join (map statement ?body))

    (?op & ?args)
    (str ?op (args-list ?args))

    ?else
    (throw (ex-info "FAIL" {:input ?else}))))

(def emit
  "Main entry point for emitting strings.
  Given an AST form produces rust code as a string."
  (s/match
    (m/pred string? ?s)
    (str "b" (pr-str ?s))

    (m/pred number? ?n)
    (str ?n)

    (m/pred symbol? ?sym)
    (maybe-mutable-ref ?sym)

    (& ?list)
    (emit-list ?list)

    ?else
    (throw (ex-info "FAIL" {:input ?else}))))
