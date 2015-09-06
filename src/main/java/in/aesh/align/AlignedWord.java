package in.aesh.align;

final class AlignedWord extends Word {
    
    public final long start;
    public final long end;
    
    AlignedWord(int turnIndex, String text, String token, long start, long end) {
        super(turnIndex, text, token);
        assert start < end;
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String toString() {
        return text + "|" + token + "|" + start + ":" + end;
    }
    
}
