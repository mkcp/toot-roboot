(ns ^{:doc "Markov data function and sentence builder function from http://diegobasch.com/fun-with-markov-chains-and-clojure"
      :author "Elle Patella"}
  toot-roboot.core
  (:require [clojure.data.csv :refer [read-csv]]
            [clojure.walk :refer [keywordize-keys]]))

(def patterns
  {:urls #"(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?"
   :retweets #"RT "
   :main-tweets #"MT "
   :mentions #"@" ;; FIXME Potential to clash with a reply system, can't rely on tweets starting with @ though
   })

(defn map-rows [rows]
  (for [row (rest rows)]
    (zipmap (first rows) row)))

(defn matches-patterns? [tweet]
  (some #(re-find % tweet)
        (vals patterns)))

(defn load-tweets [file]
  (->> (slurp file)
       read-csv
       map-rows
       keywordize-keys
       (map :text)
       (remove matches-patterns?)))

(defn markov-data
  "Takes a sequences of tweets, creates maps of markov chains, and merges them all."
  [tweets]
  (let [maps
        (for [tweet tweets
              m (let [l (str tweet ".")
                      words (cons :start (clojure.string/split l #"\s+"))]
                  (for [p  (partition 2 1 (remove #(= "" %) words))]
                    {(first p) [(second p)]}))]
          m)]
    (apply merge-with concat maps)))

(comment "TODO: Make sure these are limited to 140 characters and devise a way to blacklist words.")
(defn sentence
  "Generates a sentence at random from the markov-data map"
  [data]
  (loop [ws (data :start)
         acc []]
    (let [w (rand-nth ws)
          nws (data w)
          nacc (concat acc [w])]
      (if (= \. (last w))
        (clojure.string/join " " nacc)
        (recur nws nacc)))))

(def memo-markov-data
  (markov-data (load-tweets "resources/tweets.csv")))

(comment "Barfs out a markovalicious tweet!")
(sentence memo-markov-data)

(defn serialize-markov-data-whitespace
  "Captures stdout on pprint to write a whitespaced serialization of markov-data"
  []
  (->> (load-tweets "resources/tweets.csv")
       markov-data
       clojure.pprint/pprint
       with-out-str
       (spit "resources/markov-structure.edn")))
