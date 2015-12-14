# toot-roboot

Unfinished markov twitter bot written in Clojure.

Referring to [kelseyq's clojure twitter bot](https://github.com/kelseyq/clojure-twitter-bot/blob/mike-jones/src/clojure_twitter_bot/core.clj) and [Fun with Markov Chains and Clojure](http://diegobasch.com/fun-with-markov-chains-and-clojure) for implentation ideas and guidance.

## Usage
Copy `tweets.csv` from a twitter archive request to resources/tweets.csv.
Run `memoize-markov-data` to load-tweets and create a Clojure map representation of markov chains.

## Features
* Turn raw twitter archive into tree data structure represting markov chains.
* Create 'good' tweets from your archive, filtering out links, mentions, and retweets.

## TODO
* Config for blacklisted words and custom filter patterns.
* Optimize the chain generation algorithm for tweets.
* Reimplement `markov-data` using the processes in markov-scalable.clj Taken from the 'scaling-up' article http://diegobasch.com/markov-chains-in-clojure-part-2-scaling-up
* Finish integration with twitter-api and use a creds.edn map file or Environ.
* Set up a reasonable deployment system, with -main and configuration for tweet frequency.
* Publish with an example tweets.csv seed in resources.

## Long Term TODOs
* Reply to mentions with something akin to a legitimate response.
* Tailor to consuming coll of tweets rather than a paragraph
* TODO: Reject tweet if it contains any words from a collection defined in config
* TODO: Limit to 140 characters
* Profile why some generations hang the JVM forever 

## License
CC0, no rights reserved. Please respect the author(s) intent by using this software responsibility.
