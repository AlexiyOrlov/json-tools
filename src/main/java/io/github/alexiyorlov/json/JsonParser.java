package io.github.alexiyorlov.json;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A primitive JSON parser. Use {@link #parseObject} if the input is an object, and {@link #parseArray}
 * if the input is an array
 */
public class JsonParser {

    /**
     * @param input json
     * @return array
     */
    public List<Object> parseArray(String input) {
        List<Object> objects = new ArrayList<>();

        //first, split the string on commas
        List<String> parts = parseElements(input);

        //second, parse
        for (String part : parts) {
            Object converted = convert(part);
            objects.add(converted);
        }
        return objects;
    }

    /**
     * Converts strings into Java objects if possible
     */
    Object convert(String value) {
        if (value.equals("null")) {
            return null;
        } else if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        } else if (value.matches("[0-9]+\\.?[0-9]*")) //is number?
        {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else
                return Integer.parseInt(value);
        } else if (value.startsWith("[") && value.endsWith("]")) //is array?
        {
            List<String> list = parseElements(value);
            ArrayList<Object> objects = new ArrayList<>(list.size());
            //convert nested elements
            for (String s : list) {
                objects.add(convert(s));
            }
            return objects;
        } else if (value.startsWith("{") && value.endsWith("}")) //is object?
        {
            return parseObject(value);
        } else if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) //is string
            return value.substring(1, value.length() - 1);
        else
            return value; //is string
    }

    /**
     * @param input arrayString
     * @return list of elements as strings
     */
    List<String> parseElements(String input) {
        String string = "";
        boolean insideString = false;
        boolean insideArray = false;
        boolean insideObject = false;

        ArrayList<String> parts = new ArrayList<>();

        //remove array delimiters
        input = input.substring(1, input.length() - 1);
        for (int index = 0; index < input.length(); index++) {
            char chr = input.charAt(index);
            if (chr == '[') {
                string += '[';
                insideArray = true;
            } else if (chr == ']') {
                string += ']';
                insideArray = false;
            } else if (chr == '{') {
                insideObject = true;
                string += '{';
            } else if (chr == '}') {
                if (insideObject) {
                    insideObject = false;
                    string += '}';
                }
            } else if (chr == '"') {
                if (insideObject)
                    string += '"';
                insideString = !insideString;
            } else if (chr == ',') {
                if (!insideArray && !insideObject) {
                    if (insideString) {
                        string += ',';
                    } else {
                        parts.add(string);
                        string = "";
                    }
                } else {
                    string += ',';
                }
            } else if (chr == '\\') {
                //TODO handle more special characters
                char ahead = input.charAt(index + 1);
                if (ahead == '"') {
                    index++;
                    string += '"';
                } else if (ahead == 'u') {
                    String uni = input.substring(index + 2, index + 6);
                    char uc = (char) Integer.parseInt(uni, 16);
                    string += uc;
                    index += uni.length() + 1;
                } else if (ahead == 'n') {
                    index++;
                    string += '\n';
                }
            } else if (chr == ' ') {
                if (insideString)
                    string += ' ';
            } else {
                string += chr;
            }
        }
        if (!string.isEmpty()) {
            parts.add(string);
        }

        return parts;
    }

    /**
     * Recursively parses an object
     *
     * @param input object as string
     */
    public Map<String, Object> parseObject(String input) {
        Map<String, Object> objectMap = new HashMap<>();

        List<String> pairs = parsePairs(input);

        Map<String, String> stringPairs = new HashMap<>();

        //split pairs
        for (String pair : pairs) {
            boolean insideString = false;
            String key = "";
            String value = "";
            boolean keyOrValue = true;
            boolean insideObject = false;
            for (int i = 0; i < pair.length(); i++) {
                char c = pair.charAt(i);
                if (c == '"') {
                    insideString = !insideString;
                } else if (c == '{') {
                    insideObject = true;
                } else if (c == '}') {
                    insideObject = false;
                } else if (c == ':') {
                    if (!insideObject) {
                        if (!insideString) {
                            keyOrValue = false;
                        }
                    }
                }

                if (keyOrValue) {
                    key += c;
                } else {
                    value += c;
                }

            }
            //if it is string
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1)
                value = value.substring(1, value.length() - 1);
            stringPairs.put(key, value);
        }
        stringPairs.forEach((s, s2) -> {
            s = s.substring(1, s.length() - 1);
            //remove trailing comma
            if (s2.endsWith(","))
                s2 = s2.substring(0, s2.length() - 1);
            //remove leading ':'
            Object value = convert(s2.substring(1));
            objectMap.put(s, value);
        });

        return objectMap;
    }

    /**
     * @return list of pairs in string form
     */
    List<String> parsePairs(String input) {
        String string = "";
        boolean insideString = false;
        boolean insideObject = false;
        boolean insideArray = false;
        int arrayCounter = 0;
        int objectCounter = 0;
        ArrayList<String> parts = new ArrayList<>();

        input = input.substring(1, input.length() - 1);

        //split string on commas
        for (int i = 0; i < input.length(); i++) {
            char chr = input.charAt(i);
            if (chr == '{') {
                insideObject = true;
                string += '{';
                objectCounter++;
            } else if (chr == '}') {
                insideObject = false;
                string += '}';
                objectCounter--;
            } else if (chr == '[') {
                insideArray = true;
                string += '[';
                arrayCounter++;
            } else if (chr == ']') {
                insideArray = false;
                string += ']';
                arrayCounter--;
            } else if (chr == '"') {
                insideString = !insideString;
                string += '"';
            } else if (chr == ',') {

                string += ',';
                if (!insideObject && !insideArray) {
                    if (!insideString && arrayCounter == 0 && objectCounter == 0) {

                        parts.add(string);
                        string = "";
                    }
                }
            } else if (chr == ' ') {
                if (insideString)
                    string += ' ';
            } else {
                string += chr;
            }
        }
        if (!string.isEmpty()) {
            if (objectCounter > 0) {
                string += '}';
            }
            parts.add(string);
        }
        return parts;
    }

    /**
     * @param file file path
     * @return Json as string
     */
    public static String readJson(String file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String string;
            StringBuilder builder = new StringBuilder();
            while ((string = bufferedReader.readLine()) != null) {
                builder.append(string);
            }
            fileReader.close();
            bufferedReader.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
