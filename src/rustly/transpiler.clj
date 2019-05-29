(ns rustly.transpiler
  (:require [clojure.string :as string]
            [meander.strategy.delta :as m]))

(declare translate)

(defn constructor? [s]
  (string/ends-with? s "."))

(defn drop-trailing-dot [s]
  (string/replace s #"\.$" ""))

(defn method? [s]
  (string/starts-with? s "."))

(defn drop-leading-dot [s]
  (string/replace s #"^\." ""))

(defn args-list [args]
  (str "(" (string/join ", " (map translate args)) ")"))

(defn construct [t args]
  (str t "::new" (args-list args)))

(defn method [m obj args]
  (str (translate obj) "." m (args-list args)))

(defn translate-statement [form]
  (str (translate form) ";\n"))

(def require-clause
  "[foo.bar :refer [baz]] => use foo::bar{baz};"
  (m/rewrite
   [?library :refer [!symbols ...]]
   ~(str "use " (string/replace ?library "." "::")
         "::{" (string/join ", " !symbols) "};\n")))

(defn annotate-let [x]
  (let [{:keys [mut tag]} (meta x)]
    (str (when mut "mut ")
         x
         (when tag (str ": " tag)))))

(defn maybe-mutable-ref [x]
  (str (when (:mut (meta x)) "&mut ")
       x))

(def translate-import
  "The Clojure ns form is notoriously nuanced.
  For now only a frequently used subset is covered."
  (m/rewrite
   (:require . !clause ...)
   ~(string/join
     (map require-clause !clause))))

(def translate-list
  "Most interesting Clojure expressions are lists"
  (m/rewrite
   ;; (Type.)
   ((pred constructor? ?type) . !args ...)
   ~(construct (drop-trailing-dot ?type) !args)

   ;; (new Type)
   ('new ?type . !args ...)
   ~(construct ?type !args)

   ;; (.method obj)
   ((pred method? ?m) ?obj . !args ...)
   ~(method (drop-leading-dot ?m) ?obj !args)

   ;; (. obj method)
   ('. ?obj ?m . !args ...)
   ~(method ?m ?obj !args)

   ;; (fn foo [])
   ('fn ?name ?args . !body ...)
   ~(str "fn " ?name (args-list ?args) " {" \newline
         (string/join (map translate-statement !body))
         "}" \newline)

   ;; (let [x 1])
   ;; TODO: Clojure reader reads quote as a separate thing, weird...
   ('let [!to !from ...] . !body ...)
   ~(string/join
     (concat
      (map
       (partial format "let %s = %s;\n")
       (map annotate-let !to)
       (map translate !from))
      (map translate !body)))

   ;; (ns foo.bar (:require [baz :as b]))
   ('ns ?name . !imports ...)
   ~(string/join (map translate-import !imports))

   ;; (f x)
   (?op . !args ...)
   ~(str ?op (args-list !args))))

(def translate
  "Main entry point for transpilation.
  Given a Clojure form, produces rust code as a string."
  (m/rewrite
   ;; "a string"
   (pred string? ?s)
   ~(str "b" (pr-str ?s))

   ;; '(1 2 3)
   ('quote . !tail ...)
   ~(str "List::new()"
         (string/join (for [item (reverse !tail)]
                        (format ".push_front(%s)"
                                (translate item)))))

   ;; [1 2 3]
   (pred vector? ?vector)
   ~(str "Vector::new()"
         (string/join (for [item ?vector]
                        (format ".push_back(%s)"
                                (translate item)))))

   ;; {:a 1, :b 2}
   (pred map? ?map)
   ~(str "HashTrieMap::new()"
         (string/join (for [[k v] ?map]
                        (format ".insert(%s, %s)"
                                (translate k)
                                (translate v)))))

   ;; #{:a :b :c}
   (pred set? ?set)
   ~(str "HashTrieSet::new()"
         (string/join (for [item ?set]
                        (format ".insert(%s)"
                                (translate item)))))

   ;; (f x)
   (pred list? ?list)
   ~(translate-list ?list)

   ;; anything else
   ?x
   ~(maybe-mutable-ref ?x)))
