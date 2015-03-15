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

(defn zip
  "Turns CSV rows into maps, using the header row for the keys"
  [rows]
  (for [row (rest rows)]
    (zipmap (first rows) row)))

(defn matches-patterns? [tweet]
  (some #(re-find % tweet)
        (vals patterns)))

(defn load-tweets
  "Reads in the tweet archive, zips the rows into maps, keywordizes the maps, creates a seq of the tweet's text, and then returns tweets that aren't removed by user-defined patterns.
  The map to text conversion is wasteful in this context, would be more efficient to just seq the text column. Should probably replace zip and save for other uses."
  [file]
  (->> (slurp file)
       read-csv
       zip
       keywordize-keys
       (map :text)
       (remove matches-patterns?)))

(defn split
  "Splits a tweet into words and labels the beginning with :start"
  [tweet]
  (cons :start (clojure.string/split tweet #"\s+")))

;; TODO Decouple the map creation from the merging
(defn markov-data
  "Takes a sequences of tweets, creates maps of markov chains, and merges them all."
  [tweets]
  (let [maps
        (for [tweet tweets
              m (for [p (partition 2 1 (remove #(= "" %) (split tweet)))]
                  {(first p) [(second p)]})]
          m)]
    (apply merge-with concat maps)))

(comment "TODO: Reject tweet if it contains any words from a collection defined in config")
(comment "TODO: Limit to 140 characters")
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

(comment "TODO Reimplement `markov-data` using the processes below. Taken from the 'scaling-up' article http://diegobasch.com/markov-chains-in-clojure-part-2-scaling-up")
(comment "TODO tailor to consuming coll of tweets rather than a paragraph")
(defn transform [words]
  (->> words
       (partition 2 1)
       (reduce  (fn [acc [w next-w]]
                  (update-in acc
                             [w next-w]
                             (fnil inc 0)))
               {})))

(defn markers [line]
  (concat  [:start]
          (clojure.string/split line #"\s+")
          [:end]))

(defn lazy-lines [file]
  (letfn  [(helper [rdr]
             (lazy-seq
               (if-let [line  (.readLine rdr)]
                 (concat  (markers line)  (helper rdr))
                 (do  (.close rdr) nil))))]
    (helper  (clojure.java.io/reader file))))
