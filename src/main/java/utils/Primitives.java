package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import distributions.Bernoulli;
import distributions.Normal;

//Credits: Claudio @ Anthropic
public class Primitives {

    public interface Primitive extends Function<List<Object>, Object> {}

    private static final Map<String, Primitive> TABLE = new HashMap<>();

    // Static initializer block to populate the table
    static {
        TABLE.put("normal", args -> new Normal(
            ((Number) args.get(0)).floatValue(), 
            ((Number) args.get(1)).floatValue()
        ));
        
        TABLE.put("+", args -> {
            float sum = 0f;
            for(Object a : args){
                if(a instanceof Boolean b){
                    sum += b ? 1f : 0f;
                } else {
                    sum += ((Number) a).floatValue();
                }
            }
            return sum;
        });

        TABLE.put("bernoulli", args -> new Bernoulli(((Number) args.get(0)).floatValue()));
    }

    public static boolean isPrimitive(String name) {
        return TABLE.containsKey(name);
    }

    public static Primitive getPrimitive(String name) {
        return TABLE.get(name);
    }
}