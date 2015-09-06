package in.aesh.align;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class UnalignedWordsCollector 
implements Collector<List<UnalignedWord>, ImmutableList.Builder<Segment>, ImmutableList<Segment>> {

    private final long start;
    private final long end;
    private final long unitDuration;
    
    UnalignedWordsCollector(long start, long end, int units) {
        this.start = start;
        this.end = end;
        this.unitDuration = (end - start) / units;
    }
    
    @Override
    public Supplier<ImmutableList.Builder<Segment>> supplier() {
        return ImmutableList.Builder<Segment>::new;
    }

    @Override
    public BiConsumer<ImmutableList.Builder<Segment>, List<UnalignedWord>> accumulator() {
        return (builder, words) -> {
            List<Segment> segments = builder.build();
            long s = segments.isEmpty() ? start : segments.get(segments.size() - 1).end + 1;
            long e = s + (words.size() * unitDuration);
            builder.add(new Segment(words, s, e));
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
        return builder -> {
            ImmutableList<Segment> segments = builder.build();
            if (segments.isEmpty()) return segments;
            Segment last = segments.get(segments.size() - 1);
            if (last.end == end) return segments;
            ImmutableList.Builder<Segment> newBuilder = new ImmutableList.Builder<>();
            newBuilder.addAll(segments.subList(0, segments.size() - 1));
            newBuilder.add(last.setEnd(end));
            return newBuilder.build();
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    
}
