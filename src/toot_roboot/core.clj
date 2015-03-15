(ns toot-roboot.core
  (:require [clojure.data.csv :refer [read-csv]]
            [clojure.walk :refer [keywordize-keys]]))

(comment "Markov data function and sentence builder function from http://diegobasch.com/fun-with-markov-chains-and-clojure")

(defn map-rows [rows]
  (for [row (rest rows)]
    (zipmap (first rows) row)))

(comment "FIXME extract the patterns out of these repetitive filter
         functions and write a non-macro way to apply them all")
(defn not-retweet? [text]
  (let [pattern #"RT "]
    (not (re-find pattern text))))

(defn not-url? [text]
  (let [pattern #"(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?"]
    (not (re-find pattern text))))

(defn not-mention? [text]
  (let [pattern #"@"]
    (not (re-find pattern text))))

(defn load-tweets [file]
  (->> (slurp file)
       read-csv
       map-rows
       keywordize-keys
       (map :text)
       (filter (every-pred not-retweet? not-url? not-mention?))))

(defn markov-data
  "Takes a string and builds a markov chain map"
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

(defn serialize-markov-data
  "Warning: super kludgly. Pprints markov data and captures on stdout then writes this serialization to 'resources/markov-structure.edn'"
  []
  (->> (pprint (markov-data tweet-text))
       with-out-str
       (spit "resources/markov-structure.edn")))

(comment "Barfs out a markovalicious tweet!")
(sentence memo-markov-data)
