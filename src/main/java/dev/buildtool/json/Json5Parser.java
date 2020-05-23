package dev.buildtool.json;

/**
 * Created on 5/22/20.
 */
public class Json5Parser
{
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
}
