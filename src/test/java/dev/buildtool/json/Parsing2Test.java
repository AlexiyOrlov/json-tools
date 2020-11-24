package dev.buildtool.json;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class Parsing2Test {

    @Test
    public void test1() throws SyntaxError, Json5SyntaxError {
        Object o = new Json5Parser(Functions.readJson(Paths.get("src", "test", "fail.json5"))).parse();
        System.out.println(o);
        System.out.println(new Json5Parser(Functions.readJson(Paths.get("src", "test", "example.json5"))).parse());

        String json5Data = Functions.readJson(Paths.get("path", "to", "file"));
        Json5Parser json5Parser = new Json5Parser(json5Data);
        Object result = json5Parser.parse();

    }
}
