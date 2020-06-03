package dev.buildtool.json;

public class Json5SyntaxError extends Exception {
    private final Json5Parser json5Parser;
    int lineNumber, columnNumber;

    Json5SyntaxError(Json5Parser json5Parser, String message_) {
        super(message_);
        this.json5Parser = json5Parser;
        this.lineNumber = json5Parser.line;
        this.columnNumber = json5Parser.column;
    }
}
