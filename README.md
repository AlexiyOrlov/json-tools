# JSON tools

A small library for working with JSON5 data. Currently includes a parser,
which converts the data to a List or a Map, and a serializer, which converts a Map or a List to JSON5 data. 
The parser and serializer have been adapted from the original JavaScript functions.

# Requirements

Java 9 is needed by this project.

# Usage

## Parsing

```java
String json5Data = Functions.readJson(Paths.get("path", "to", "file"));
Json5Parser json5Parser = new Json5Parser(json5Data);
Object result = json5Parser.parse();
//or if you know the input type:
List<Object> list=json5Parser.parseAsList();
Map<String,Object> map=json5Parser.parseAsMap();
```

## Serializing

```java
List<?> list=...;
Json5Serializer serializer=new Json5Serializer(list);
String result=serializer.serialize();
Map<?,?> map=...;
//set the indent size to 6
Json5Serializer mapSer=new Json5Serializer(map,6);
String result2=mapSer.serialize();
```

[![Maven Central](https://img.shields.io/maven-central/v/dev.buildtool/json-tools.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22dev.buildtool%22%20AND%20a:%22json-tools%22)