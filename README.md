Tools for aligning speech audio with a transcript using the [Sphinx4](http://cmusphinx.sourceforge.net) speech recognition engine.

Running `make` will:

* Create a JSON transcript without speech timings
* Convert the MP3 audio to WAV (PCM signed 16-bit little-endian, mono, 16kHz)
* Align the WAV audio and the transcript, updating the transcript file
* Convert the WAV audio back to MP3 suitable for web streaming
* Prepare transcript for use in [react-transcript-player](https://github.com/contours/react-transcript-player)

It relies on [libav](https://libav.org), [LAME](http://lame.sourceforge.net), and:

----
`create-transcript-json.py`

Creates an untimed transcript JSON file from a file with information on speakers and a file with one transcript sentence per line.

----
`build/libs/alignment.jar`

This jar can be built using [Gradle](https://gradle.org), see `build.gradle`. It takes three arguments: the path to an audio file, the path to an alignment file, and the path to a transcript file. The audio file and the transcript file must exist. If the alignment file does not exist, it will be created, otherwise the existing alignments will be used. An aligned transcript will be printed to standard out. See the `Makefile` for an example of usage.

----
`prepare-transcript.py`

Prepares a transcript JSON file for use with [react-transcript-player](https://github.com/contours/react-transcript-player) , adding a link to the audio file, titlecasing speakers' names, and fixing issues in speech timings, e.g.: `./prepare-transcript.py out/u-0080/transcript.json`
