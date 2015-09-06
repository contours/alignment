package in.aesh.align;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class Segment {

    private final ImmutableList<Word> words;
    final long start;
    final long end;
        
    Segment(AlignedWord word) {
        this.words = ImmutableList.of(word);
        this.start = word.start;
        this.end = word.end;
    }
    
    Segment(List<UnalignedWord> words, long start, long end) {
        this.words = ImmutableList.copyOf(words);
        this.start = start;
        this.end = end;
    }
    
    Segment(Segment segment, long start, long end) {
        this.words = segment.words;
        this.start = start;
        this.end = end;
    }
    
    Segment setStart(long start) {
        return new Segment(this, start, this.end);
    }
    
    Segment setEnd(long end) {
        return new Segment(this, this.start, end);
    }
    
    int getTurnIndex() {
        assert words.size() > 0;
        int turnIndex = this.words.get(0).turnIndex;
        assert words.stream().allMatch(word -> word.turnIndex == turnIndex);
        return turnIndex;
    }
    
    String getText() {
        return words.stream()
                .map(word -> word.text)
                .collect(Collectors.joining(" "));
    }
    
    ImmutableList<String> getTokens() {
        return ImmutableList.copyOf(
                words.stream()
                        .map(word -> word.token)
                        .collect(Collectors.toList()));
    }
    
    ImmutableList<Segment> splitAtTurnBoundaries() {
        if (words.stream().allMatch(word -> word instanceof AlignedWord)) {
            assert words.size() == 1;
            return ImmutableList.of(this);
        }
        return words.stream()
                .map(word -> (UnalignedWord) word)
                .collect(Collectors.groupingBy(word -> word.turnIndex))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getValue())
                .collect(new UnalignedWordsCollector(start, end, words.size()));
    }
    
    @Override
    public String toString() {
        return "{" + start + ":" + end + "|" + getText() + "}";
    }
}