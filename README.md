entity-matcher

An efficient String matching library for matching 'entities'  using the Aho-Corasick algorithm.
In addition to matching strings in a single pass, the framework has(will have)
ability to manage relationships across entities and use clues like
capitlazation, surrounded by quotes to make final decisions as in a true
NER system

So basically an NER-like library that doesn't use any fancy CRF/Markov models, just uses
string matching and relationships between entities in a Naive Bayes fashion to
determine if we truly find a match for the things we're looking for


