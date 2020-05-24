package dev.buildtool.json;

import org.junit.jupiter.api.Test;

/**
 * Created on 5/24/20.
 */
public class Json5ParserTest
{

    @Test
    public void testParse() throws Json5Parser.SyntaxError
    {
        String array = JsonParser.readJson("src/test/array5.json5");
        Json5Parser json5Parser = new Json5Parser();
        System.out.println(json5Parser.parse(array));
    }
}