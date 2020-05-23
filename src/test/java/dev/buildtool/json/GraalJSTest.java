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
    public void testJavaScriptParser() throws Exception
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
            if (string.contains("\\u"))
            {
                final int beginIndex = string.indexOf("\\u");
                String sub = string.substring(beginIndex + 2, beginIndex + 6);
                char c = (char) Integer.parseInt(sub, 16);
                final String str = string.replaceFirst("\\\\u.{4}", String.valueOf(c));
                combined.append(str);
            }
            else
            {
                combined.append(string);
            }
        }
        System.out.println("From: " + combined.toString());
        Value value = context.eval("js", "parse('" + combined.toString() + "')");
        System.out.println("To: " + value);
        context.close();
    }
}
