(ns toot-roboot.core
  (:require [twitter.oauth :refer :all]
            [twitter.callbacks :refer :all]
            [twitter.callbacks.handlers :refer :all]
            [twitter.api.restful :refer :all]))



(defn load-credentials
  "FIXME: Needs error handling."
  []
  (:fake-creds (read (slurp "resources/creds.edn"))))

(defn make-credentials
  [{:keys [consumer-key consumer-secret app-key app-secret]}]
  (make-oauth-creds consumer-key
                    consumer-secret
                    app-key
                    app-secret))

(def my-creds (make-credentials (load-credentials)))

(def config
  {:archive-location "resources/tweets.csv"
   :patterns {:urls #"(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?"
              :retweets #"RT "
              :main-tweets #"MT "
              :mentions #"@"}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Archive loading
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn rows->maps
  "Turns CSV rows into maps, using the header row for the keys"
  [rows]
  (for [row (rest rows)]
    (zipmap (first rows) row)))

(defn matches-patterns? [tweet]
  (some #(re-find % tweet)
        (vals (:patterns config))))

(defn load-tweets
  "Converts the tweet archive file into a sequence of tweet strings."
  [file]
  (->> (slurp file)
       clojure.data.csv/read-csv
       rows->maps
       clojure.walk/keywordize-keys
       (map :text)
       (remove matches-patterns?)))

(defn split
  "Splits a tweet into words and labels the beginning with :start"
  [tweet]
  (cons :start (clojure.string/split tweet #"\s+")))

(defn make-maps [tweets]
  (for [tweet tweets
        m (for [p (partition 2 1 (remove #(= "" %)
                                         (split tweet)))]
            {(first p) [(second p)]})]
    m))

(defn build
  "Takes a sequences of tweets, creates maps of markov chains, and merges them all."
  [tweets]
  (apply merge-with concat (make-maps tweets)))


;; Save tweet data in a variable
;; FIXME Replace this with something implicit as part of the creation
(def markov-tree
  (-> (:archive-location config)
      load-tweets
      build))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; Tweet
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn generate-sentence
  "Begins at nodes marked :start and ends with a period."
  [data]
  (loop [ws (data :start)
         acc []]
    (let [w (rand-nth ws)
          nws (data w)
          nacc (concat acc [w])]
      (if (= \. (last w))
        (clojure.string/join " " nacc)
        (recur nws nacc)))))

(defn make-tweet []
  (let [tweet (generate-sentence markov-tree)]
    (if (< 140 (count tweet))
      (recur)
      tweet)))

;; DEBUG
#_(make-tweet)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO Serialize with options rather than being so specific
(defn serialize-markov-data-with-whitespace
  "Writes a prettified serialization of the markov tree to `resources/markov-data.edn`"
  []
  (->> (:archive-location config)
       load-tweets
       build
       clojure.pprint/pprint
       with-out-str
       (spit "test/test_files/markov-data.edn")))

#_(defn -main
    "Accepts tweet-frequency in milliseconds to schedule tweeting."
    [tweet-frequency])
