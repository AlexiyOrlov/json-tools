package dev.buildtool.json;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class Parsing2Test {

    @Test
    public void test1() throws Json5Parser2.SyntaxError {
        new Json5Parser2(Json5Parser.readJson(Paths.get("src", "test", "fail.json5"), true)).parse();
    }
}
