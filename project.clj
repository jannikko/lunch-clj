(defproject lunch "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                 [org.clojure/clojurescript "1.9.227"]
                 [org.clojure/core.async "0.2.374"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [org.clojure/tools.logging  "0.3.1"]
                 [org.slf4j/slf4j-api "1.6.4"]
                 [ch.qos.logback/logback-classic "1.0.1"]
                 [reagent "0.5.1"]
                 [binaryage/devtools "0.6.1"]
                 [re-frame "0.7.0"]
                 [secretary "1.2.3"]
                 [compojure "1.5.0"]
                 [cljs-http "0.1.41"]
                 [yogthos/config "0.8"]
                 [postgresql "9.3-1102.jdbc41"]
                 [ragtime "0.6.0"]
                 [slingshot "0.12.2"]
                 [yesql "0.5.3"]
                 [ring "1.4.0"]
                 [ring/ring-mock "0.3.0"]
                 [ring/ring-defaults "0.2.1"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [cider/cider-nrepl "0.12.0"]
            [lein-ring  "0.9.7"]
            [lein-less "1.7.5"]]

  :min-lein-version "2.5.3"

  :test-paths ["test"]

  :aliases {"migrate"  ["run" "-m" "user/migrate"]
            "rollback" ["run" "-m" "user/rollback"]}

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler lunch.handler/dev-handler}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :profiles
  {:dev
   {:dependencies [[com.cemerick/piggieback  "0.2.1"]
                   [figwheel-sidecar "0.5.2"]]
    :resource-paths ["src/config/dev"]
    :plugins      [[lein-figwheel "0.5.4-3"]]
    :main user
    }
   :client {:prep-tasks [["cljsbuild" "once" "min"] "compile"]}
   }

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "lunch.core/mount-root"}
     :compiler     {:main                 lunch.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :jar true
     :compiler     {:main            lunch.core
                    :output-to       "resources/public/js/compiled/app.js"
                    ;; advanced compilation not working yet 
                    :optimizations   :whitespace
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    ]}

  :main lunch.server

  :uberjar-name "lunch.jar"

  :aot [lunch.server]

  )
