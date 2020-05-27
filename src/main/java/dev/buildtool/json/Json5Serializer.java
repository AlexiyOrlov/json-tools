package dev.buildtool.json;

import java.util.*;

public class Json5Serializer {
    ArrayList<Object> stack = new ArrayList<>();
    String indent = "";
    List<String> propertyList;
    String gap = "";
    Object value;

    public Json5Serializer(Object value) {
        this.value = value;
        gap = "    ";
    }

    public Json5Serializer(Object value, int space) {
        double d = Math.min(10, Math.floor(space));
        gap = "          ".substring(0, (int) d);
        this.value = value;
    }

    public String serialize() {
        HashMap<String, Object> hashMap = new HashMap<>(1, 1);
        hashMap.put("", value);
        return serializeProperty("", hashMap);
    }

    static Map<Character, String> replacements = new HashMap<>();

    static {
        replacements.put('\'', "\\");
        replacements.put('"', "\\");
        replacements.put('\\', "\\\\");
        replacements.put('\b', "\\b");
        replacements.put('\f', "\\f");
        replacements.put('\n', "\\n");
        replacements.put('\r', "\\r");
        replacements.put('\t', "\\t");
//        replacements.put('\v')
        replacements.put('\0', "\\0");
        replacements.put('\u2028', "\\2028");
        replacements.put('\u2029', "\\20298");
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    String serializeProperty(String key, Object holder) {
        Object value = null;
        if (holder instanceof List) {
            value = ((List) holder).get(Integer.parseInt(key));
        } else if (holder instanceof Map) {
            value = ((Map) holder).get(key);
        }

        if (value == null)
            return "null";
        if (Boolean.TRUE == value) {
            return "true";
        } else if (Boolean.FALSE == value) {
            return "false";
        }

        if (value instanceof String) {
            return quoteString((String) value);
        } else if (value instanceof Integer || value instanceof Double) {
            return String.valueOf(value);
        }
        if (value instanceof List) {
            return serializeArray((List<Object>) value);
        }

        if (value instanceof Map) {
            return serializeObject((Map<String, Object>) value);
        }

        return null;
    }

    String quoteString(String value) {
        Map<Character, Float> quotes = new LinkedHashMap<>(2, 1);
        quotes.put('\'', 0.1f);
        quotes.put('"', 0.2f);

        StringBuilder product = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\'':
                case '"': {
                    quotes.compute(c, (character, aFloat) -> aFloat++);
                    product.append(c);
                    continue;
                }
                case '\0':
                    if (Functions.isDigit(String.valueOf(value.charAt(i + 1)))) {
                        product.append("\\x00");
                        continue;
                    }
            }

            String replacement = replacements.get(c);
            if (replacement != null) {
                product.append(replacement);
                continue;
            }

            if (c < ' ') {
                String hexString = String.valueOf((int) c);
                product.append("\\x").append(("00" + hexString).substring(hexString.length()));
                continue;
            }

            product.append(c);
        }

        final Optional<Character> quoteChar = quotes.keySet().stream().reduce((character, character2) -> character < character2 ? character : character2);
        if (quoteChar.isPresent()) {
            Character qch = quoteChar.get();
            product = new StringBuilder(product.toString().replaceAll(String.valueOf(qch), replacements.get(qch)));
            return qch + product.toString() + qch;
        }
        return product.toString();
    }

    String serializeObject(Map<String, Object> value) {
        if (stack.contains(value))
            throw new IllegalArgumentException("Converting circular structure to JSON5");
        stack.add(value);
        String stepback = indent;
        indent = indent + gap;
        List<String> keys;
        if (propertyList != null)
            keys = propertyList;
        else
            keys = new ArrayList<>(value.keySet());

        ArrayList<String> partial = new ArrayList<>();

        for (final String key : keys) {
            final String propertyString = serializeProperty(key, value);
            if (propertyString != null) {
                String member = serializeKey(key) + ':';
                if (!gap.equals("")) {
                    member += ' ';
                }
                member += propertyString;
                partial.add(member);
            }
        }

        String final_;
        if (partial.size() == 0) {
            final_ = "{}";
        } else {
            String properties;
            if (gap.equals("")) {
                properties = String.join(",", partial);
                final_ = '{' + properties + '}';
            } else {
                String separator = ",\n" + indent;
                properties = String.join(separator, partial);
                final_ = "{\n" + indent + properties + ",\n" + stepback + '}';
            }
        }

        stack.remove(stack.size() - 1);
        indent = stepback;
        return final_;
    }

    String serializeKey(String key) {
        if (key.length() == 0)
            return quoteString(key);

        final char firstChar = key.charAt(0);
        if (!Functions.isIdStartChar(firstChar))
            return quoteString(key);

        //TODO check
        if (!Functions.isIdContinueChar(firstChar))
            return quoteString(key);

        return key;
    }

    String serializeArray(List<Object> value) {
        if (stack.contains(value)) {
            throw new IllegalArgumentException("Converting circular structure to JSON5");
        }

        stack.add(value);

        String stepback = indent;
        indent = indent + gap;
        ArrayList<String> partial = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            //TODO
            final String propertyString = serializeProperty(String.valueOf(i), value);
            partial.add(propertyString == null ? "null" : propertyString);
        }

        String final_;
        if (partial.size() == 0) {
            final_ = "[]";
        } else {
            if (gap.equals("")) {
                final_ = '[' + String.join(",", partial) + ']';
            } else {
                String separator = ",\n" + indent;
                final_ = "[\n" + indent + String.join(separator, partial) + ",\n" + stepback + ']';
            }
        }
        stack.remove(stack.size() - 1);
        indent = stepback;
        return final_;
    }
}
