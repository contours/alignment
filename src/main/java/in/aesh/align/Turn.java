package in.aesh.align;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Stream;

class Turn {
    
    final int speaker;
    final ImmutableList<String> sentences;

    Turn(int speaker, List<String> sentences) {
        this.speaker = speaker;
        this.sentences = ImmutableList.copyOf(sentences);
    }
    
    Stream<String> getTextStream() {
        return sentences.stream()
                .flatMap(sentence -> Stream.of(sentence.split("\\s")));
    }
    

}
