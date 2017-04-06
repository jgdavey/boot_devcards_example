(set-env!
 :source-paths #{"src/cljs" "test/clj"}
 :resource-paths #{"src/clj" "src/cljc" "resources"}
 :dependencies '[[org.clojure/clojure "1.9.0-alpha15" :scope "provided"]
                 [boot/core "2.7.1" :scope "test"]
                 [org.clojure/clojurescript "1.9.495" :scope "test"]
                 [adzerk/boot-cljs "2.0.0" :scope "test"]
                 [adzerk/boot-reload "0.5.1" :scope "test"]
                 [adzerk/boot-test "1.2.0" :scope "test"]
                 [pandeiro/boot-http "0.7.6"]
                 [sablono "0.8.0"]
                 [devcards "0.2.1-6"]
                 [org.omcljs/om "1.0.0-alpha48" :exclusions [org.clojure/clojure]]
                 [adzerk/boot-cljs-repl "0.3.2" :scope "test"]
                 [com.cemerick/piggieback "0.2.1" :scope "test" :exclusions [org.clojure/clojurescript org.clojure/clojure]]
                 [weasel "0.7.0" :scope "test" :exclusions [org.clojure/clojurescript org.clojure/clojure]]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test" :exclusions [org.clojure/clojure]]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl repl-env]]
  '[adzerk.boot-reload    :refer [reload]]
  '[adzerk.boot-test      :refer [test]]
  '[pandeiro.boot-http    :refer [serve]])

(task-options!
  pom {:project 'carder
       :version "0.1.0-SNAPSHOT"
       :description "Example application"
       :license {"The MIT License (MIT)" "http://opensource.org/licenses/mit-license.php"}}
  aot {:namespace #{'backend.main}}
  jar {:main 'backend.main}
  cljs {:source-map true})

(deftask dev
  "Start the dev env..."
  [s speak           bool "Notify when build is done"
   p port       PORT int  "Port for web server"]
  (comp
    (watch)
    (reload :on-jsload 'carder.core/main)
    (cljs-repl)
    (cljs :compiler-options {:devcards true})
    (serve :port port, :dir "target")
    (if speak (boot.task.built-in/speak) identity)))

(deftask run-tests []
  (test))

(deftask autotest []
  (comp
    (watch)
    (run-tests)))

(deftask package
  "Build the package"
  []
  (comp
    (cljs :optimizations :advanced)
    (aot)
    (pom)
    (uber)
    (jar)))


