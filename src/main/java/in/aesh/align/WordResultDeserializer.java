package in.aesh.align;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.cmu.sphinx.linguist.dictionary.Pronunciation;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import java.lang.reflect.Type;

class WordResultDeserializer implements JsonDeserializer<WordResult> {

    @Override
    public WordResult deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) {
        JsonObject o = (JsonObject) json;
        String token = o.getAsJsonPrimitive("word").getAsString();
        long start = o.getAsJsonPrimitive("start").getAsLong();
        long end = o.getAsJsonPrimitive("end").getAsLong();
        return new WordResult(new Word(token, new Pronunciation[0], false), new TimeFrame(start, end), 0.0, 0.0);
    }
    
}
