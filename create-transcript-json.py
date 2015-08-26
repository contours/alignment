#! /usr/bin/env python3

import sys
import json
import itertools


def unique(iterable):
    seen = set()
    for x in itertools.filterfalse(seen.__contains__, iterable):
        seen.add(x)
        yield x

with open(sys.argv[1]) as speakers_file:
    sentence_speakers = [l.strip() for l in speakers_file.readlines()]
with open(sys.argv[2]) as sentence_file:
    sentences = [l.strip() for l in sentence_file.readlines()]
assert(len(sentences) == len(sentence_speakers))

speakers = tuple(unique(sentence_speakers))
sentence_speaker_indexes = [speakers.index(s) for s in sentence_speakers]
indexed_sentences = zip(sentence_speaker_indexes, sentences)

transcript = {
    'speakers': speakers,
    'turns': [
        {'speaker': speaker_index, 'sentences': [x[1] for x in turn_sentences]}
        for speaker_index, turn_sentences in itertools.groupby(
            indexed_sentences, lambda x: x[0])]
    }

print(json.dumps(transcript, indent=2))
