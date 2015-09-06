/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.aesh.align;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author ryanshaw
 */
class AlignedTurnSerializer implements JsonSerializer<AlignedTurn> {

    @Override
    public JsonElement serialize(AlignedTurn turn, Type type, JsonSerializationContext ctx) {
        JsonObject o = new JsonObject();
        o.addProperty("speaker", turn.speaker);
        o.addProperty("start", turn.start);
        o.addProperty("end", turn.end);
        o.add("sentences", turn.sentences.stream()
                .map(sentence -> new JsonPrimitive(sentence))
                .collect(new JsonArrayCollector()));
        o.add("speech", turn.speech.stream()
                .map(segment -> ctx.serialize(segment))
                .collect(new JsonArrayCollector()));
        return o;
    }
    
}
