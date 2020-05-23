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
}
