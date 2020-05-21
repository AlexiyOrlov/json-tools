package io.github.alexiyorlov.json5;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class ParserTest {
    @Test
    void testObject() {
        try {
            FileReader fileReader = new FileReader("src/test/object0.json");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String string;
            StringBuilder pretty = new StringBuilder();
            StringBuilder builder = new StringBuilder();
            while ((string = bufferedReader.readLine()) != null) {
                builder.append(string);
                pretty.append(string).append('\n');
            }
            fileReader.close();
            bufferedReader.close();
            System.out.println(pretty.toString());
            String single = builder.toString();
            System.out.println("One-liner: " + single + '\n');

            if (single.startsWith("{")) {
                Map<String, Object> pairs = Parser.parseObject(single);
                System.out.println("Result: ");
                System.out.println("{");
                pairs.forEach((s, o) -> System.out.println(s + " : " + o));
                System.out.println("}");
            }
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    @Test
    void testArray() {
        try {
            FileReader fileReader = new FileReader("src/test/array0.json");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String string;
            StringBuilder pretty = new StringBuilder();
            StringBuilder builder = new StringBuilder();
            while ((string = bufferedReader.readLine()) != null) {
                builder.append(string);
                pretty.append(string).append('\n');
            }
            fileReader.close();
            bufferedReader.close();

            System.out.println(pretty.toString());
            String single = builder.toString();
            System.out.println("One-liner: " + single + '\n');

            if (single.startsWith("[")) {
                List<Object> objects = Parser.parseArray(single);
                System.out.println("Result: ");
                objects.forEach(System.out::println);
            }
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    @Test
    void testOthers() {
        String array = Parser.readJson("src/test/array.json");
        String object2 = Parser.readJson("src/test/object2.json");
        String object1 = Parser.readJson("src/test/object1.json");
        String object3 = Parser.readJson("src/test/object3.json");
        HashSet<String> everything = new HashSet<>(3);
        everything.add(array);
        everything.add(object2);
        everything.add(object3);
        everything.add(object1);
        HashSet<Object> results = new HashSet<>(everything.size());
        everything.forEach(s -> {
            if (s.startsWith("[")) {
                results.add(Parser.parseArray(s));
            } else if (s.startsWith("{")) {
                results.add(Parser.parseObject(s));
            }
        });
        results.forEach(o -> {
            if (o instanceof Map) {
                ((Map) o).forEach((o1, o2) -> {
                    System.out.println(o1 + " = " + o2);
                });
            } else if (o instanceof List) {
                ((List) o).forEach(System.out::println);
            }
            System.out.println();
        });
    }
}