(defproject lunch "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha12"]
                 [org.clojure/clojurescript "1.9.227"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/java.jdbc "0.6.2-alpha3"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [hiccup "1.0.5"]
                 [aleph "0.4.2-alpha8"]
                 [manifold "0.1.5"]
                 [jarohen/chord "0.7.0"]
                 [cheshire "5.6.3"]
                 [hikari-cp "1.7.3"]
                 [org.slf4j/slf4j-api "1.6.4"]
                 [ch.qos.logback/logback-classic "1.0.1"]
                 [reagent "0.5.1"]
                 [ring/ring-json "0.4.0"]
                 [binaryage/devtools "0.6.1"]
                 [re-frame "0.7.0"]
                 [secretary "1.2.3"]
                 [compojure "1.5.0"]
                 [clojurewerkz/route-one "1.2.0"]
                 [cljs-http "0.1.41"]
                 [yogthos/config "0.8"]
                 [ragtime "0.6.0"]
                 [yesql "0.5.3"]
                 [ring "1.4.0"]
                 [commons-validator "1.5.1"]
                 [com.stuartsierra/component "0.3.1"]
                 [metosin/ring-http-response "0.8.0"]
                 [ring/ring-mock "0.3.0"]
                 [ring/ring-defaults "0.2.1"]]

  :plugins [[lein-cljsbuild "1.1.4"]
            [cider/cider-nrepl "0.12.0"]
            [lein-ring "0.9.7"]
            [lein-sassy "1.0.7"]]

  :min-lein-version "2.5.3"

  :test-paths ["test"]

  :aliases {"migrate"  ["run" "-m" "user/migrate"]
            "rollback" ["run" "-m" "user/rollback"]}

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :sass {:src "sass"
         :dst "resources/public/css"}

  :profiles
  {
   :dev     {:resource-paths ["src/config/dev"]
             :plugins        [[lein-figwheel "0.5.4-3"]]
             :main           lunch.user}

   :uberjar {:resource-paths ["src/config/prod"]
             :prep-tasks     [["cljsbuild" "once" "min"] ["sass" "once"] "compile"]
             :uberjar-name   "lunch.jar"
             :aot            :all
             :main           lunch.system}

   :client  {:prep-tasks [["cljsbuild" "once" "dev"] ["sass" "once"] "compile"]}
   }

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
     :jar          true
     :compiler     {:main            lunch.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :externs         ["externs/google_maps_api_v3.js"]
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}
     }
    ]}

  :clean-non-project-files false
  )
