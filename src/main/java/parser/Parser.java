package parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import exception.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

    private final static String TARGET_KEY = "value";
    private final static String ISSUE_KEY = "issue";
    private final static String RESOLVER_KEY = "resolver";

    public List<String> ParseJson(String filename) throws NotFoundException {
        final ArrayList<String> targets = new ArrayList<>();
        final Stack<String> parents = new Stack<>();

        try {
            final JsonParser parser = initParser(filename);

            while (parser.nextToken() != null) {
                final JsonToken currentToken = parser.currentToken();
                final String key = parser.getCurrentName();

                if (currentToken == JsonToken.START_OBJECT) {
                    if (key == null) {
                        parents.push("null");
                    } else {
                        parents.push(key);
                    }
                }

                if (currentToken == JsonToken.END_OBJECT) {
                    parents.pop();
                }

                if (currentToken == JsonToken.FIELD_NAME && key.equals(TARGET_KEY)) {
                    final int size = parents.size();
                    if (size >= 3 && parents.get(size - 1).equals(RESOLVER_KEY) && parents.get(size - 2)
                            .equals(ISSUE_KEY) && parents.get(size - 3).equals(ISSUE_KEY)) {
                        final JsonToken jsonToken = parser.nextToken();
                        if (jsonToken == JsonToken.VALUE_STRING) {
                            targets.add(parser.getText());
                        } else if (jsonToken == JsonToken.START_OBJECT) {
                            parents.push(key);
                        } else if (jsonToken == JsonToken.END_OBJECT) {
                            parents.pop();
                        }
                    }
                }
            }

            parser.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse", e);
        }

        return targets;
    }

    private JsonParser initParser(String filename) throws NotFoundException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final InputStream resourceAsStream = classloader.getResourceAsStream(filename);
        if (resourceAsStream == null) {
            throw new NotFoundException("File not found");
        }

        JsonFactory jsonfactory = new JsonFactory();
        try {
            return jsonfactory.createParser(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create parser");
        }
    }
}
