package in.aesh.align;

import com.google.gson.GsonBuilder;
import edu.cmu.sphinx.alignment.LongTextAligner;
import edu.cmu.sphinx.api.SpeechAligner;
import edu.cmu.sphinx.result.WordResult;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Main {
    
    private static final OptionParser parser;
    private static final NonOptionArgumentSpec<File> FILE_ARGUMENTS;
    private static final String ACOUSTIC_MODEL_PATH;
    private static final String DICTIONARY_PATH;

    static {
        parser = new OptionParser();
        FILE_ARGUMENTS = parser.nonOptions("[audio file] [alignment file] [transcript file]").ofType(File.class);
        ACOUSTIC_MODEL_PATH = "resource:/edu/cmu/sphinx/models/en-us/en-us";
        DICTIONARY_PATH = "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    }

    public static void main(String args[]) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        new Main(args).run();
    }

    private final URL audioUrl;
    private final Transcript transcript; 
    private final GsonBuilder gson;
    private final File alignmentFile;
    private AlignmentResults results = null;
    
    public Main(String[] args) throws IOException {
        
        OptionSet options = parser.parse(args);
        if (options.valuesOf(FILE_ARGUMENTS).size() != 3) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }
        File audioFile = options.valuesOf(FILE_ARGUMENTS).get(0);
        alignmentFile = options.valuesOf(FILE_ARGUMENTS).get(1);
        File transcriptFile = options.valuesOf(FILE_ARGUMENTS).get(2);
        
        gson = new GsonBuilder();
        gson.setPrettyPrinting();
        
        gson.registerTypeAdapter(WordResult.class, new WordResultDeserializer());
        gson.registerTypeAdapter(WordResult.class, new WordResultSerializer());
        gson.registerTypeAdapter(Turn.class, new TurnDeserializer());
        gson.registerTypeAdapter(AlignedTurn.class, new AlignedTurnSerializer());
        gson.registerTypeAdapter(Segment.class, new SegmentSerializer());
        
        audioUrl = audioFile.toURI().toURL();
        transcript = gson.create().fromJson(new FileReader(transcriptFile), Transcript.class);
        
        if (alignmentFile.exists()) {
            results = gson.create().fromJson(new FileReader(alignmentFile), AlignmentResults.class);
        }
    }

    private long getAudioDuration() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(audioUrl);
        assert(stream.getFormat().getEncoding() == AudioFormat.Encoding.PCM_SIGNED);
        DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(),
                ((int) stream.getFrameLength() * stream.getFormat().getFrameSize()));
        try (Clip clip = (Clip) AudioSystem.getLine(info)) {
            return (long) (clip.getBufferSize() / 
                    (clip.getFormat().getFrameSize() * clip.getFormat().getFrameRate())
                    * 1000);
        }
    }
    
    private void run() throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        SpeechAligner aligner = new SpeechAligner(ACOUSTIC_MODEL_PATH, DICTIONARY_PATH, null);
        
        String text = transcript.getText().replace("--", "");
        List<String> tokens = aligner.sentenceToWords(aligner.getTokenizer().expand(text));
        
        if (results == null) {
            results = new AlignmentResults();
            results.words = aligner.align(audioUrl, text);
            List<String> stringResults = results.words.stream()
                    .map(wr -> wr.getWord().getSpelling())
                    .collect(Collectors.toList());
            results.indexes = new LongTextAligner(stringResults, 2).align(tokens);
            Files.write(
                    alignmentFile.toPath(), 
                    gson.create().toJson(results).getBytes(StandardCharsets.UTF_8));
        }
        
        long duration = getAudioDuration();
        Transcript alignedTranscript = transcript.align(tokens, results, duration);
        System.out.println(gson.create().toJson(alignedTranscript));
    }
}
