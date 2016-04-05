# Language recognizer

## Features
Based on the data it should recognize in which language the given input is. N-Gram model is used for 1 <= n <= 3
and the base unit is one word.

## Feature extraction
The project contains another java project in directory ngrams which can extract and save mention ngrams from text into a json file.
For that extraction the embedded java library for GATE is used and text is cut into words.

Then the ngram model is evaluated giving probabilities for particular ngrams and the whole data set is saved into a json file.
The file contains a hash with top leve keys = languages and then under one language there are three keys = unigrams, bigrams, trigrams
containing the extracted ngrams with their probabilities.

## Searching
The root directory, if placed in the web server document directory (supporting php 5.5), can start the
php application for searching. Try to navigate to localhost/<the directory with this app>/www/ to start it.

It loads the json file into the memory and for every input it evaluates the sentence against the model printing out
the rankings of all languages. If no language is recognized it warns user that it could not find a suitable language.

[ddw/Screenshot from 2016-04-06 00:27:54.png](screenshot)
