package in.aesh.align;

import com.google.common.collect.ImmutableList;
import edu.cmu.sphinx.util.TimeFrame;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Transcript {

    private String media;
    private List<String> speakers;
    private List<Turn> turns;
     
    private Transcript() {} // required for GSON
    
    private Transcript(String media, List<String> speakers, List<Turn> turns) {
        this.media = media;
        this.speakers = speakers;
        this.turns = turns;
    }
    
    public String getText() {
        return String.join("\n", turns.stream()
                .map(turn -> String.join("\n", turn.sentences))
                .collect(Collectors.toList()));
    }
    
    private List<Word> getWords() {
        return turns.stream()
                .flatMap(turn -> turn.getTextStream()
                        .map(text -> new Word(turns.indexOf(turn), text, text)))
                .collect(Collectors.toList());
    }
    
    public Transcript align(List<String> tokens, AlignmentResults results, long duration) {
        List<Word> words = getWords();
        assert words.size() == tokens.size();
        assert tokens.size() == results.indexes.length;
        List<Turn> alignedTurns = IntStream.range(0, words.size())
                .mapToObj(i -> {
                    Word word = words.get(i);
                    String token = tokens.get(i);
                    if (results.indexes[i] < 0) {
                        return new UnalignedWord(
                                word.turnIndex, word.text, token);
                    } else {
                        TimeFrame t = results.words.get(results.indexes[i]).getTimeFrame();
                        return new AlignedWord(
                                word.turnIndex, word.text, token, t.getStart(), t.getEnd());
                    }
                }).collect(new WordCollector(duration)).stream()
                .flatMap(segment -> segment.splitAtTurnBoundaries().stream())
                .collect(new SegmentCollector(turns));
        return new Transcript(
                this.media, 
                ImmutableList.copyOf(this.speakers),
                alignedTurns);
    }  
}