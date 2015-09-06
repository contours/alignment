package in.aesh.align;

import com.google.common.collect.ImmutableList;

final class AlignedTurn extends Turn {

    final ImmutableList<Segment> speech;
    final long start;
    final long end;

    AlignedTurn(Turn turn, ImmutableList<Segment> segments) {
        super(turn.speaker, turn.sentences);
        assert segments.size() > 0;
        this.speech = segments;
        this.start = segments.get(0).start;
        this.end = segments.get(segments.size() - 1).end;
    }
}
