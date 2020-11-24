package dev.buildtool.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created on 5/24/20.
 */
public class Json5ParserTest
{

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testParse() throws Json5SyntaxError, SyntaxError, IOException {
        Set<Path> paths = Files.list(Paths.get("src", "test")).collect(Collectors.toSet());
        for (Path path : paths) {
            if (Files.isRegularFile(path)) {
                String content = Functions.readJson(path);
                System.out.println(new Json5Parser(content).parse());
            }
        }
    }
}