package dev.buildtool.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class Functions {

    static boolean isDigit(String s) {
        return s.matches("[0-9]");
    }

    static boolean isIdStartChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '$' || c == '_') || Json5Parser.ID_START.matcher(String.valueOf(c)).matches();
    }

    static boolean isIdContinueChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '$') || (c == '_') || (c == '\u200C') || (c == '\u200D') || Json5Parser.ID_CONTINUE.matcher(String.valueOf(c)).matches();
    }

    /**
     * @param file file path
     * @return Json as string
     */
    public static String readJson(String file) {
        return readJson(Paths.get(file));
    }

    /**
     * @param path Json file
     * @return Json as string
     */
    public static String readJson(Path path) {
        try {
            BufferedReader reader = Files.newBufferedReader(path);
            String string;
            StringBuilder builder = new StringBuilder();
            while ((string = reader.readLine()) != null) {
                builder.append(string);
                builder.append('\n');
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Writes Json string to a named file
     *
     * @return the file
     */
    public static Path writeJson(String json, String fileName) {
        Path path = Paths.get(fileName);
        if (Files.notExists(path)) {
            try {
                Files.write(path, Collections.singleton(json));
                return path;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
