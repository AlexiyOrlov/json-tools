package dev.buildtool.json;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class Parsing2Test {

    @Test
    public void test1() throws Json5Parser.SyntaxError, Json5SyntaxError {
        Object o = new Json5Parser(Functions.readJson(Paths.get("src", "test", "fail.json5"))).parse();
        System.out.println(o);
        System.out.println(new Json5Parser(Functions.readJson(Paths.get("src", "test", "example.json5"))).parse());

    }
}
