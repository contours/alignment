package in.aesh.align;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import edu.cmu.sphinx.result.WordResult;
import java.lang.reflect.Type;

class WordResultSerializer implements JsonSerializer<WordResult> {

    @Override
    public JsonElement serialize(WordResult wr, Type type, JsonSerializationContext ctx) {
        JsonObject o = new JsonObject();
        o.addProperty("word", wr.getWord().getSpelling());
        
        o.addProperty("start", wr.getTimeFrame().getStart());
        o.addProperty("end", wr.getTimeFrame().getEnd());
        return o;
    }
    
}
