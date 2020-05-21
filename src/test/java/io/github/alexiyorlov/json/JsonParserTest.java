package io.github.alexiyorlov.json;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class JsonParserTest {
    @Test
    public void testObject() {

        String string = JsonParser.readJson("src/test/object0.json");
        System.out.println("One-liner: " + string + '\n');
        if (string != null && string.startsWith("{")) {
            Map<String, Object> pairs = new JsonParser().parseObject(string);
            System.out.println("Result: ");
            System.out.println("{");
            pairs.forEach((s, o) -> System.out.println(s + " : " + o));
            System.out.println("}");
        }
    }

    @Test
    public void testArray() {

        String string = JsonParser.readJson("src/test/array0.json");
        System.out.println("One-liner: " + string + '\n');
        if (string != null && string.startsWith("[")) {
            List<Object> objects = new JsonParser().parseArray(string);
            System.out.println("Result: ");
            objects.forEach(System.out::println);
        }
    }

    @Test
    public void testOthers() {
        String array = JsonParser.readJson("src/test/array.json");
        String object2 = JsonParser.readJson("src/test/object2.json");
        String object1 = JsonParser.readJson("src/test/object1.json");
        String object3 = JsonParser.readJson("src/test/object3.json");
        HashSet<String> everything = new HashSet<>(3);
        everything.add(array);
        everything.add(object2);
        everything.add(object3);
        everything.add(object1);
        everything.add(JsonParser.readJson("src/test/big_object.json"));
        HashSet<Object> results = new HashSet<>(everything.size());
        JsonParser jsonParser = new JsonParser();
        everything.forEach(s -> {
            if (s.startsWith("[")) {
                results.add(jsonParser.parseArray(s));
            } else if (s.startsWith("{")) {
                results.add(jsonParser.parseObject(s));
            }
        });
        results.forEach(o -> {
            if (o instanceof Map) {
                ((Map) o).forEach((o1, o2) -> {
                    System.out.println(o1 + " = " + o2);
                });
            } else if (o instanceof List) {
                ((List) o).forEach(System.out::println);
            }
            System.out.println();
        });
    }
}