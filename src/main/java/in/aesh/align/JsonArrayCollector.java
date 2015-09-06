package in.aesh.align;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class JsonArrayCollector
implements Collector<JsonElement, JsonArray, JsonArray> {

    @Override
    public Supplier<JsonArray> supplier() {
        return JsonArray::new;
    }

    @Override
    public BiConsumer<JsonArray, JsonElement> accumulator() {
        return (array, element) -> array.add(element);
    }

    @Override
    public BinaryOperator<JsonArray> combiner() {
        return (a1, a2) -> {
            throw new UnsupportedOperationException("Not supported.");
        };
    }

    @Override
    public Function<JsonArray, JsonArray> finisher() {
        return array -> array;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.IDENTITY_FINISH);
    }
    
}
