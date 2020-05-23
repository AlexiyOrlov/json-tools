package dev.buildtool.json;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created on 5/22/20.
 */
public class GraalJSTest
{
    @Test
    public void testStringsWithQuotes() throws URISyntaxException, IOException
    {
        Context context = Context.newBuilder("js").build();
        String version = context.getEngine().getLanguages().get("js").getVersion();
        System.out.println(version);
        Source source = Source.newBuilder("js", new File(getClass().getClassLoader().getResource("parse.js").toURI())).build();
        context.eval(source);
        String dqs = "\"Str\\\"ing\"";
        Value value = context.eval("js", "parse('[" + Json5Parser.handleQuotes(dqs) + "]')");
        System.out.println(value);
        String sqs = "\"Str'ing\"";
        value = context.eval("js", "parse('[" + Json5Parser.handleQuotes(sqs) + "]')");
        System.out.println(value);
        context.close();
    }

    @Test
    public void testJSON5Parser() throws Exception
    {
        Context context = Context.newBuilder("js").build();
        String version = context.getEngine().getLanguages().get("js").getVersion();
        System.out.println(version);
        Source source = Source.newBuilder("js", new File(getClass().getClassLoader().getResource("parse.js").toURI())).build();
        context.eval(source);
        List<String> strings = Files.readAllLines(Path.of("src/test/array5.json5"));
        StringBuilder combined = new StringBuilder();
        for (String string : strings)
        {
            string = Json5Parser.handleQuotes(string);
            string = Json5Parser.handleUnicode(string);
            combined.append(string);
        }
        System.out.println("From: " + combined.toString());
        Value value = context.eval("js", "parse('" + combined.toString() + "')");
        System.out.println("To: " + value.as(List.class));
        System.out.println();

        List<String> list = Files.readAllLines(Path.of("src/test/big_object.json"));
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : list)
        {
            str = Json5Parser.handleQuotes(str);
            str = Json5Parser.handleUnicode(str);
            stringBuilder.append(str);
        }
        Value v = context.eval("js", "parse('" + stringBuilder.toString() + "')");
        System.out.println(v.as(Map.class));
        context.close();
    }
}
