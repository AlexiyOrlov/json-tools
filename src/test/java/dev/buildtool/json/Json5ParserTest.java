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
        ArrayList<Object> results = new ArrayList<>();
        results.add(new Json5Parser().parse(array));
//        results.add(new Json5Parser().parse(arrayWithObject));
        results.add(new Json5Parser().parse(object));
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