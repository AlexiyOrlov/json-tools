package dev.buildtool.json;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created on 5/22/20.
 */
public class Json5Parser
{
    /**
     * Parses a Json file. Context returned in the pair must be closed after manipulations with the object
     *
     * @param file Json file
     * @return a pair of context and List, if the input is an array, or Map, if the input is object
     */
    public Pair<Object, Context> parse(Path file)
    {
        try
        {
            List<String> lines = Files.readAllLines(file);
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : lines)
            {
                s = handleQuotes(s);
                s = handleUnicode(s);
                s = handleNewline(s);
                stringBuilder.append(s);
            }
            String combined = stringBuilder.toString();
            Context context = Context.newBuilder("js").build();

            final URL jsParser = getClass().getClassLoader().getResource("parse.js");
            if (jsParser == null)
            {
                throw new NullPointerException("Didn't find the parsing script");
            }
            else
            {
                Source source = Source.newBuilder("js", new File(jsParser.toURI())).build();
                context.eval(source);
                Value value = context.eval("js", "parse('" + combined + "')");
                if (combined.startsWith("["))
                {
                    return new Pair<>(value.as(List.class), context);
                }
                else if (combined.startsWith("{"))
                {
                    return new Pair<>(value.as(Map.class), context);
                }
            }
        }
        catch (IOException | URISyntaxException ioException)
        {
            ioException.printStackTrace();
        }
        return null;
    }

    /**
     * Escapes inside quotes
     *
     * @param input string with quotes
     * @return string with escaped quotes
     */
    public static String handleQuotes(final String input)
    {
        String ret;
        if (input.contains("\\\""))
        {
            ret = input.replace("\\\"", "\\\\\"");
            return ret;
        }
        else if (input.contains("'"))
        {
            ret = input.replace("'", "\\'");
            return ret;
        }
        return input;
    }

    /**
     * Replaces unicode sequence with appropriate character
     */
    public static String handleUnicode(final String input)
    {
        if (input.contains("\\u"))
        {
            final int beginIndex = input.indexOf("\\u");
            String sub = input.substring(beginIndex + 2, beginIndex + 6);
            char c = (char) Integer.parseInt(sub, 16);
            return input.replaceFirst("\\\\u.{4}", String.valueOf(c));
        }
        return input;
    }

    public static String handleNewline(final String input)
    {
        if (input.contains("\\n"))
        {
            return input.replace("\\n", "\\\n");
        }
        return input;
    }
}
