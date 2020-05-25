package dev.buildtool.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 5/24/20.
 */
public class Json5ParserTest
{

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testParse() throws Json5Parser.SyntaxError
    {
        String array = JsonParser.readJson("src/test/array5.json5", true);
        String object = JsonParser.readJson("src/test/object.json5", true);
        String arrayWithObject = JsonParser.readJson("src/test/array0.json", true);
        String objectWithArray = JsonParser.readJson("src/test/object0.json", true);
        String bigObject = JsonParser.readJson("src/test/big_object.json", true);
        String object5 = JsonParser.readJson("src/test/object5.json5", true);
        ArrayList<Object> results = new ArrayList<>();
        results.add(new Json5Parser().parse(bigObject));
        results.add(new Json5Parser().parse(array));
        results.add(new Json5Parser().parse(arrayWithObject));
        results.add(new Json5Parser().parse(object));
        results.add(new Json5Parser().parse(object5));
        results.add(new Json5Parser().parse(objectWithArray));
        System.out.println("Results: ");
        results.forEach(result -> {
            if (result instanceof List)
            {
                ((List) result).forEach(System.out::println);
            }
            else if (result instanceof Map)
            {
                ((Map) result).forEach((o, o2) -> System.out.println(o + " : " + o2));
            }
            System.out.println(".............");
        });

    }
}