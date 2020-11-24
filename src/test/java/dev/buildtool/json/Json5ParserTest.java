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
    public void testParse() throws Json5SyntaxError, Json5Parser.SyntaxError {
        String array = Json5ParserBugged.readJson("src/test/array5.json5");
        String object = Json5ParserBugged.readJson("src/test/object.json5");
        String arrayWithObject = Json5ParserBugged.readJson("src/test/array0.json");
        String objectWithArray = Json5ParserBugged.readJson("src/test/object0.json");
        String bigObject = Json5ParserBugged.readJson("src/test/big_object.json");
        String object5 = Json5ParserBugged.readJson("src/test/object5.json5");
        String officialExample = Json5ParserBugged.readJson("src/test/example.json5");
        ArrayList<Object> results = new ArrayList<>();
        results.add(new Json5Parser(bigObject).parse());
        results.add(new Json5Parser(array).parse());
        results.add(new Json5Parser(arrayWithObject).parse());
        results.add(new Json5Parser(object).parse());
        results.add(new Json5Parser(object5).parse());
        results.add(new Json5Parser(objectWithArray).parse());
        results.add(new Json5Parser(officialExample).parse());
        results.add(new Json5Parser(Json5ParserBugged.readJson("src/test/array.json")).parse());
        results.add(new Json5Parser(Json5ParserBugged.readJson("src/test/object1.json")).parse());
        results.add(new Json5Parser(Json5ParserBugged.readJson("src/test/object2.json")).parse());
        results.add(new Json5Parser(Json5ParserBugged.readJson("src/test/object5.json5")).parse());
        final Json5Parser parser = new Json5Parser(Json5ParserBugged.readJson("src/test/object3.json"));
        results.add(parser.parse());
        System.out.println("Results: ");
        results.forEach(result -> {
            if (result instanceof List) {
                ((List) result).forEach(System.out::println);
            } else if (result instanceof Map) {
                ((Map) result).forEach((o, o2) -> System.out.println(o + " : " + o2));
            }
            System.out.println(".............");
        });
    }
}