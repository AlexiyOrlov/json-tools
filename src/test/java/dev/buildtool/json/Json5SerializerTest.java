package dev.buildtool.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Json5SerializerTest {

    @Test
    public void testSerialize() throws Json5SyntaxError, SyntaxError {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add("string");
        arrayList.add(55);
        arrayList.add(8.95);
        arrayList.add(null);
        arrayList.add(false);
        arrayList.add(true);

        List<Object> nestedList = new ArrayList<>();
        nestedList.add("sequence\n");
        nestedList.add(2685);
        arrayList.add(nestedList);

        String result = new Json5Serializer(arrayList, 3).serialize();
        System.out.println(result);

        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("one", true);
        linkedHashMap.put("two", false);
        linkedHashMap.put("three", null);
        linkedHashMap.put("four", 333);
        linkedHashMap.put("five", 11.44);
        linkedHashMap.put("uni", "\u0dca");
        linkedHashMap.put("four+five", 9);
        String resultt = new Json5Serializer(linkedHashMap, 3).serialize();
        System.out.println(resultt);

        Object array = new Json5Parser(Functions.readJson(Paths.get("src", "test", "array5.json5"))).parse();
        String serialized = new Json5Serializer(array).serialize();
        System.out.println(serialized);
        new Json5Parser(serialized).parse();
    }

    @Test
    public void testWrite() throws IOException {
        String json = "{one:1,\ntwo:2,\n'three+four':7}";
        Path path = Functions.writeJson(json, "testWrite.json5");
        Files.delete(path);
    }
}