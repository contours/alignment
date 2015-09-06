package in.aesh.align;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class WordCollector 
implements Collector<Word, ImmutableList.Builder<Segment>, ImmutableList<Segment>> {

    private final List<UnalignedWord> unalignedWords;
    private final long duration;
    
    WordCollector(long duration) {
        this.unalignedWords = new ArrayList<>();
        this.duration = duration;
    }
    
    private ImmutableList.Builder<Segment> addUnalignedWords(
            ImmutableList.Builder<Segment> builder, long end) {
        if (! unalignedWords.isEmpty()) {
            List<Segment> segments = builder.build();
            long start = segments.isEmpty() ? 0L : segments.get(segments.size() - 1).end + 1;
            builder.add(new Segment(unalignedWords, start, end));
            unalignedWords.clear();
        }
        return builder;
    }
    
    private ImmutableList.Builder<Segment> addAlignedWord(
            ImmutableList.Builder<Segment> builder, AlignedWord word) {
        addUnalignedWords(builder, word.start - 1);
        Segment next = new Segment(word);
        List<Segment> segments = builder.build();
        if (! segments.isEmpty()) {
            Segment last = segments.get(segments.size() - 1);
            if (next.start <= last.end) {
                next = next.setStart(last.end + 1);
            }
        }
        builder.add(next);
        return builder;
    }

    @Override
    public Supplier<ImmutableList.Builder<Segment>> supplier() {
        return ImmutableList.Builder<Segment>::new;
    }

    @Override
    public BiConsumer<ImmutableList.Builder<Segment>, Word> accumulator() {
        return (builder, word) -> {
            if (word instanceof AlignedWord) {
                addAlignedWord(builder, (AlignedWord) word);
            } else { // word is unaligned
                unalignedWords.add((UnalignedWord) word);
            }
        };
    }

    @Override
    public BinaryOperator<ImmutableList.Builder<Segment>> combiner() {
        return (b1, b2) -> {
                throw new UnsupportedOperationException("Not supported.");
        };
    }

    @Override
    public Function<ImmutableList.Builder<Segment>, ImmutableList<Segment>> finisher() {
        return builder -> addUnalignedWords(builder, duration).build();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

}
