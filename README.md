# toot-roboot

Unfinished markov twitter bot written in Clojure.

Referring to [kelseyq's clojure twitter bot](https://github.com/kelseyq/clojure-twitter-bot/blob/mike-jones/src/clojure_twitter_bot/core.clj) and [Fun with Markov Chains and Clojure](http://diegobasch.com/fun-with-markov-chains-and-clojure) for implentation ideas and guidance.


## Features
* Turn raw tweets.csv into markov chain data structure.
* Create 'good' tweets from your archive, filtering out links, mentions, and retweets.

## TODO
* Configuration file for filter patterns and a list of words to exclude.
* Limit sentences creation to 140 chars, or just reject and generate new if it's too long.
* Actually integrate with twitter-api with a creds.edn map.
* Reply to mentions with something akin to a legitimate response.
* Set up a reasonable deployment system, with -main and configuration for tweet frequency.

## Usage
Copy `tweets.csv` from a twitter archive request to resources/tweets.csv.
Run `memoize-markov-data` to load-tweets and create a Clojure map representation of markov chains.

## License
CC0, no rights reserved. Please respect the author(s) intent by using this software responsibility.
