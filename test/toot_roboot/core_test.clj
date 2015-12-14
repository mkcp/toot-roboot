(ns toot-roboot.core-test
  (:require [clojure.test :refer :all]
            [toot-roboot.core :refer :all]))

(def test-markov-tree
  (-> "/test/test_data/markov-data.edn"
      slurp
      read))

(deftest tweeting-test
  (testing "tweet generation"
    (let [tweet (make-tweet test-markov-data)]
      (is (<= (count (seq tweet))
              140)))))
