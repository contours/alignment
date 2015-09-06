package in.aesh.align;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class SegmentCollector
implements Collector<Segment, ImmutableList.Builder<Turn>, ImmutableList<Turn>> {

    private final Iterator<Turn> turns;
    private final List<Segment> segments;
    
    SegmentCollector(List<Turn> turns) {
        this.turns = turns.iterator();
        this.segments = new ArrayList<>();
    }
    
    @Override
    public Supplier<ImmutableList.Builder<Turn>> supplier() {
        return ImmutableList.Builder<Turn>::new;
    }

    @Override
    public BiConsumer<ImmutableList.Builder<Turn>, Segment> accumulator() {
        return (builder, segment) -> {
            if (segments.size() > 0 && segment.getTurnIndex() != segments.get(0).getTurnIndex()) {
                addAlignedTurn(builder);
            }
            segments.add(segment);
        };
    }

    private ImmutableList.Builder<Turn> addAlignedTurn(ImmutableList.Builder<Turn> builder) {
        builder.add(new AlignedTurn(this.turns.next(), ImmutableList.copyOf(segments)));
        segments.clear();
        return builder;
    }

    @Override
    public BinaryOperator<ImmutableList.Builder<Turn>> combiner() {
        return (b1, b2) -> {
                throw new UnsupportedOperationException("Not supported.");
        };
    }

    @Override
    public Function<ImmutableList.Builder<Turn>, ImmutableList<Turn>> finisher() {
        return builder -> addAlignedTurn(builder).build();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }
    
    
}
