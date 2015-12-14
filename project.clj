(defproject toot-roboot "0.1.0-SNAPSHOT"
  :author "meat.computer"
  :description "Plug in your twitter archive, get a twitter bot."
  :url "http://example.com/FIXME"
  :license {:name "CC0, no rights reserved. Please respect the author(s) intent by using this software ethically and responsibility. "
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [twitter-api "0.7.7"]]
  :main ^:skip-aot toot-roboot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
