package in.aesh.align;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

class SegmentSerializer implements JsonSerializer<Segment> {

    @Override
    public JsonElement serialize(Segment segment, Type type, JsonSerializationContext ctx) {
        JsonObject o = new JsonObject();
        o.addProperty("text", segment.getText());
        o.addProperty("start", segment.start);
        o.addProperty("end", segment.end);
        o.add("tokens", segment.getTokens().stream()
                .map(token -> new JsonPrimitive(token))
                .collect(new JsonArrayCollector()));
        return o;
    }
    
}
