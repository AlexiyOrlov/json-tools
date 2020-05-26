package dev.buildtool.json;

public class Functions {

    static boolean isDigit(String s) {
        return s.matches("[0-9]");
    }

    static boolean isIdStartChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '$' || c == '_') || Json5Parser.idStart.matcher(String.valueOf(c)).matches();
    }

    static boolean isIdContinueChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '$') || (c == '_') || (c == '\u200C') || (c == '\u200D') || Json5Parser.idContinue.matcher(String.valueOf(c)).matches();
    }
}
