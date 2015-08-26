package in.aesh.align;

import java.util.ArrayList;
import java.util.List;

final class Speech {

    private String text;
    private List<String> tokens;
    private Long start;
    private Long end;
        
    private Speech() {} // required for GSON
    
    Speech(String text, String token, long start, long end) throws Exception {
        this(text, token, start);
        this.setEnd(end);
    }
        
    Speech(String text, String token, long start) {
        this.text = text;
        this.tokens = new ArrayList<>();
        this.tokens.add(token);
        this.start = start;
    }
        
    void add(String text, String token) {
        this.text = this.text + " " + text;
        this.tokens.add(token);
    }
    
    void setStart(long start) {
        this.start = start;
        if (this.hasEnd() && start >= this.end) {
            this.setEnd(start + 1);
        }
    }
    
    void setEnd(long end) throws Exception {
        if (end <= this.start) throw new Exception("end must be > " + this.start);
        this.end = end;
    }
    
    long getStart() {
        return this.start;
    }
    
    long getEnd() {
        return this.end;
    }
    
    boolean hasStart() {
        return this.start != null;
    }
    
    boolean hasEnd() {
        return this.end != null;
    }
    
    static final class Exception extends RuntimeException {
        Exception(String message) { super(message); }
    }
}