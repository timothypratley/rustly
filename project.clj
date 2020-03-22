(defproject rustly "0.1.1-alpha"
  :description "A Clojure to Rust transpiler"
  :url "http://github.com/timothypratley/rustly"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[meander/epsilon "0.0.408"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.reader "1.3.2"]
                 [org.clojure/tools.cli "1.0.194"]]
  :main rustly.main)
