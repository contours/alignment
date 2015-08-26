package in.aesh.align;

import edu.cmu.sphinx.result.WordResult;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Transcript {

    private String media;
    private List<String> speakers;
    private List<Turn> turns;
    private transient Speech unalignedSpeech = null;
    private transient List<Speech> allSpeech = new ArrayList<>();
    private transient long lastTimePoint = 0L;
    
    private Transcript() {} // required for GSON
    
    public String getText() {
        return String.join("\n", turns.stream()
                .map(turn -> String.join("\n", turn.sentences))
                .collect(Collectors.toList()));
    }

    private void checkTokens(List<String> tokens) {
        List <String> words = turns.stream()
                .flatMap(turn -> turn.getWords().stream())
                .collect(Collectors.toList());
        if (tokens.size() != words.size()) {
            for (int i = 0; i < Math.min(tokens.size(), words.size()); i++) {
                System.err.println(String.format("%1$-20s", words.get(i)) + tokens.get(i));
            }
            throw new RuntimeException(words.size() + " words; " + tokens.size() + " tokens");
        }
    }
    
    public void setAlignment(List<String> tokens, AlignmentResults results, long duration) {
        checkTokens(tokens);
        ListIterator<String> iterTokens = tokens.listIterator();
        turns.forEach(turn -> {
           turn.speech = new ArrayList<>();
           turn.getWords().forEach(word -> {
               int index = iterTokens.nextIndex();
               String token = iterTokens.next();
               if (results.indexes[index] != -1) {
                   WordResult result = results.words.get(results.indexes[index]);
                   assert(token.equals(result.getWord().getSpelling()));
                   Speech alignedSpeech = new Speech(word, token, 
                           result.getTimeFrame().getStart(), 
                           result.getTimeFrame().getEnd());
                   if (unalignedSpeech != null) {
                       try {
                           unalignedSpeech.setEnd(alignedSpeech.getStart());
                       } catch (Speech.Exception e) {
                           unalignedSpeech.setEnd(unalignedSpeech.getStart() + 1);
                           alignedSpeech.setStart(unalignedSpeech.getEnd() + 1);
                       }
                       turn.speech.add(unalignedSpeech);
                       allSpeech.add(unalignedSpeech);
                       unalignedSpeech = null;
                   }
                   turn.speech.add(alignedSpeech);
                   allSpeech.add(alignedSpeech);
                   lastTimePoint = alignedSpeech.getEnd();
               } else {
                   if (unalignedSpeech == null) {
                       unalignedSpeech = new Speech(word, token, lastTimePoint);
                   } else {
                       unalignedSpeech.add(word, token);
                   }
               }
           });
           if (unalignedSpeech != null) {
               turn.speech.add(unalignedSpeech);
               allSpeech.add(unalignedSpeech);
               unalignedSpeech = null;
           }
        });

        turns.stream().forEach(turn -> {
            turn.start = findStartFrom(turn.speech.get(0));
            Speech lastSpeech = turn.speech.get(turn.speech.size() - 1);
            turn.end = findEndFrom(lastSpeech, duration);
            if (! lastSpeech.hasEnd()) {
                try {
                    lastSpeech.setEnd(turn.end);
                } catch (Speech.Exception e) {
                    lastSpeech.setEnd(lastSpeech.getStart() + 1);
                }
            }
        });
        
    }
    
    private long findStartFrom(Speech speech) {
        if (speech.hasStart()) return speech.getStart();
        for (int i = this.allSpeech.indexOf(speech) - 1; i >= 0; i--) {
            Speech s = this.allSpeech.get(i); 
            if (s.hasEnd()) return s.getEnd();
            if (s.hasStart()) return s.getStart();
        }
        return 0L;
    }
    
    private long findEndFrom(Speech speech, long duration) {
        if (speech.hasEnd()) return speech.getEnd();
        for (int i = this.allSpeech.indexOf(speech) + 1; i < this.allSpeech.size(); i++) {
            Speech s = this.allSpeech.get(i); 
            if (s.hasStart()) return s.getStart();
            if (s.hasEnd()) return s.getEnd();
        }
        return duration - 1;
    }
    
    private static class Turn {

        private int speaker;
        private List<String> sentences;
        private List<Speech> speech;
        private Long start;
        private Long end;
        
        private Turn() {} // required for GSON
        
        private List<String> getWords() {
            return sentences.stream()
                    .flatMap(sentence -> Stream.of(sentence.split("\\s+")))
                    .collect(Collectors.toList());
        }
    }
    
}
