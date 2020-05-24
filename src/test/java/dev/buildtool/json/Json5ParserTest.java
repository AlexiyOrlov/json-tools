package dev.buildtool.json;

import org.junit.jupiter.api.Test;

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
        Json5Parser json5Parser = new Json5Parser();
        Object result = json5Parser.parse(array);
        System.out.println("Result: ");
        if (result instanceof List)
        {
            ((List) result).forEach(System.out::println);
        }
        else if (result instanceof Map)
        {
            ((Map) result).forEach((o, o2) -> System.out.println(o + " : " + o2));
        }
    }
}