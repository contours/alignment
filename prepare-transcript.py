#! /usr/bin/env python3

import io
import sys
import json
from itertools import tee, chain, groupby
from difflib import ndiff
from pprint import pprint

from colorama import Fore, Style


def windows(iterable):
    "s -> (s0,s1), (s1,s2), (s2, s3), ..."
    a, b, c = tee([None]+list(iterable)+[None, None], 3)
    next(c, None)
    next(c, None)
    next(b, None)
    for window in zip(a, b, c):
        yield window


def check_timing(o, next):
    if (o is None):
        return []
    problems = []
    if (o['start'] > o['end']):
        problems.append(
            '* begins after it ends: %s' % (o['start'] - o['end']))
    if next:
        if (o['start'] > next['start']):
            problems.append(
                '* begins after successor starts: %s'
                % (o['start'] - next['start']))
    return problems


def merge(objects):
    "merges temporal ranges of objects"
    if len(objects) == 0:
        return
    endpoints = list(chain.from_iterable(
        [(o['start'], o['end']) for o in objects]))
    for o in objects:
        merged = {}
        merged.update(o)
        merged.update(start=min(endpoints), end=max(endpoints))
        yield merged


def downscale_time_resolution(objects, factor=1000):
    for o in objects:
        if o is None:
            yield None
        else:
            downscaled = {}
            downscaled.update(o)
            downscaled.update(
                start=o['start'] // factor,
                end=o['end'] // factor)
            yield downscaled


def exclude(o, *keys):
    c = o.copy()
    for key in keys:
        del c[key]
    return c


def show(o, highlight=False):
    if o is None:
        return
    if highlight:
        print(Fore.RED, file=sys.stderr, end='')
    print('START:\t%s' % o['start'], file=sys.stderr)
    pprint(exclude(o, 'sentences', 'tokens'), stream=sys.stderr)
    print('END:\t%s' % o['end'], file=sys.stderr)
    if highlight:
        print(Style.RESET_ALL, file=sys.stderr, end='')


def tell(o=''):
    print(o, file=sys.stderr)


def lines_of(buf):
    return buf.getvalue().splitlines(keepends=True)


def diff(a, b):
    if a == b:
        return
    show_a = io.StringIO('')
    pprint(a, stream=show_a)
    show_b = io.StringIO('')
    pprint(b, stream=show_b)
    lines = list(ndiff(lines_of(show_a), lines_of(show_b)))
    if len(lines) > 0:
        tell('----------------------------------------')
        sys.stderr.writelines(lines)


def diff_on_key(a, b, key):
    a_value = a.get(key, None) if a else None
    b_value = b.get(key, None) if b else None
    diff(a_value, b_value)


def speech_of(o):
    for turn in o['turns']:
        for speech in turn['speech']:
            o = {}
            o.update(speech)
            o['speaker'] = turn['speaker']
            o['sentences'] = turn['sentences']
            yield o


def adjust_timings(speech):
    skip = 0
    prev, curr, next = None, None, None
    for prev, curr, next in [downscale_time_resolution(w)
                             for w in windows(speech)]:
        if skip > 0:
            skip = skip - 1
            continue
        problems = check_timing(curr, next)
        if problems:
            tell('\n----------------------------------------')
            tell('\nProblems found in speech:')
            for problem in problems:
                tell(problem)
            tell()
            show(prev)
            show(curr, highlight=True)
            show(next)
            tell('\nMerging speech:\n')
            for m in merge((prev, curr, next)):
                show(m)
                if m is not None:
                    yield m
            skip = 2
        elif prev is not None:
            yield prev
    if not skip:
        if curr is not None:
            yield curr


def prepare(o):
    p = {}

    # add link to audio file
    p['media'] = 'media/audio.mp3'

    # titlecase speakers' names
    p['speakers'] = [s.title() for s in o['speakers']]

    # fix issues in speech timings
    p['turns'] = []
    for speaker, speech in groupby(adjust_timings(speech_of(o)),
                                   key=lambda x: x['speaker']):
        speech = list(speech)
        sentences = speech[0]['sentences']
        speech = [exclude(s, 'speaker', 'sentences') for s in speech]
        turn = {'speaker': speaker,
                'sentences': sentences,
                'start': speech[0]['start'],
                'end': speech[-1]['end']}
        turn['speech'] = speech
        p['turns'].append(turn)

    return p


o = json.load(open(sys.argv[1]))
p = prepare(o)

print(json.dumps(p, indent=4))
