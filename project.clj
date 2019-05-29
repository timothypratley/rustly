(defproject rustly "0.1.0-alpha"
  :description "A Clojure to Rust transpiler"
  :url "http://github.com/timothypratley/rustly"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[meander/delta "0.0.90"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.reader "1.3.2"]
                 [org.clojure/tools.cli "0.4.2"]]
  :main rustly.core
  :repl-options {:init-ns rustly.core})
