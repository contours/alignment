all: out/u-0078/transcript.json out/u-0078/audio.mp3 out/u-0080/transcript.json out/u-0080/audio.mp3

clean:
	rm -rf \
	out \
	u-*-transcript.json \
	u-*-untimed.json \
	u-*.speakers \
	u-*.txt \
	u-*.wav

superclean: clean
	rm -f u-*-alignment.json

# Convert the MP3 from http://dc.lib.unc.edu/cdm/ref/collection/sohp/id/8193
# to PCM signed 16-bit little-endian, mono, 16kHz.
u-%.wav: U%_Audio.mp3
	avconv \
		-i $< \
		-acodec pcm_s16le \
		-ac 1 \
		-ar 16000 \
		$@

u-%.speakers: 
	cp ../corenlp/data/in/U-$*.speakers $@

u-%.txt:
	./cleantext.sed < ../corenlp/data/in/U-$*.txt > $@

# Create a JSON transcript without speech timings.
u-%-untimed.json: u-%.speakers u-%.txt create-transcript-json.py
	./create-transcript-json.py $+ | jq --sort-keys '' > $@

# Align the audio and the transcript, updating the transcript file.
u-%-transcript.json: u-%.wav u-%-untimed.json build/libs/alignment.jar
	java -ea -Xmx8g -jar build/libs/alignment.jar \
		$< u-$*-alignment.json $(word 2,$^) \
		2>> alignment.log \
		> $@

# Convert back to MP3 for use in the webapp.
out/u-%/audio.mp3: u-%.wav
	mkdir -p out/u-$*
	lame $< $@

# Prepare transcript for use in webapp.
out/u-%/transcript.json: u-%-transcript.json prepare-transcript.py
	mkdir -p out/u-$*
	./prepare-transcript.py $< 2>> preparation.log > $@
