package in.aesh.align;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class TurnDeserializer implements JsonDeserializer<Turn> {

    @Override
    public Turn deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) {
        JsonObject o = (JsonObject) json;
        int speaker = o.getAsJsonPrimitive("speaker").getAsInt();
        List<String> sentences = StreamSupport.stream(
                o.getAsJsonArray("sentences").spliterator(), false)
                .map((element) -> element.getAsString())
                .collect(Collectors.toList());
        return new Turn(speaker, sentences);
    }
    
}
