(ns toot-roboot.markov)

;;; Markov something
(defn markov-data
  "Splits a text into a map of markov chains"
  [text]
  (let [maps
        (for [line (clojure.string/split text #"\.")
              m (let [l (str line  ".")
                      words (cons :start (clojure.string/split l #"\s+"))]
                  (for [p  (partition 2 1 (remove #(= "" %) words))]
                    {(first p) [(second p)]}))]
          m)]
    (apply merge-with concat maps)))

;; TODO This can consume any map, replace markov-data with something that reads from edn
(defn sentence
  "Consumes the maps made from markov-data to create a sentence"
  [data]
  (loop [ws (data :start)
         acc []]
    (let [w (rand-nth ws)
          nws (data w)
          nacc (concat acc [w])]
      (if (= \. (last w))
        (clojure.string/join " " nacc)
        (recur nws nacc)))))

(sentence (markov-data "This is a test example"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Line marking from a file
;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn markers  [line]
  (concat [:start]
          (clojure.string/split line #"\s+")
          [:end]))

(defn lazy-lines  [file]
  (let [rdr (clojure.java.io/reader file)]
    (lazy-seq
      (if-let  [line (.readLine rdr)]
        (concat (markers line)
                (helper rdr))
        (do (.close rdr) nil)))))
