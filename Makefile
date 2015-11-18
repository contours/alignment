all: u-0078-transcript.json u-0078.mp3 u-0080-transcript.json u-0080.mp3

clean:
	rm -f \
	u-*-transcript.json \
	u-*-untimed.json \
	u-*.mp3 \
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
u-%-untimed.json: u-%.speakers u-%.txt
	./create-transcript-json.py $+ | jq --sort-keys '' > $@

# Align the audio and the transcript, updating the transcript file.
u-%-transcript.json: u-%.wav u-%-untimed.json
	java -ea -Xmx8g -jar build/libs/alignment.jar \
		$< u-$*-alignment.json $(word 2,$^) \
		2>> alignment.log \
		> $@

# Convert back to MP3 for use in the webapp.
u-%.mp3: u-%.wav
	lame $<

