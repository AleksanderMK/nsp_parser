package parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

    private final static String TARGET_KEY = "value";
    private final static String ISSUE_KEY = "issue";
    private final static String RESOLVER_KEY = "resolver";

    /**
     * Parses a JSON document provided by the given input
     * stream and tries to look for {@code [*.]issue.issue.resolver.value}.
     *
     * @param inputStream target json data
     * @return all the occurrences of {@code [*.]issue.issue.resolver.value}, if any.
     * Empty list is returned when no results found.
     */
    public List<String> parseJson(InputStream inputStream) throws IOException {
        final ArrayList<String> targets = new ArrayList<>();
        //stack for storing json objects hierarchy
        final Stack<String> parents = new Stack<>();

        try {
            final JsonParser parser = initParser(inputStream);

            //read json
            while (parser.nextToken() != null) {
                final JsonToken currentToken = parser.currentToken();
                final String key = parser.getCurrentName();

                //if json object starts - add field name to stack
                if (currentToken == JsonToken.START_OBJECT) {
                    if (key == null) {
                        //if json token key is null add pseudo key, uses in comparing
                        parents.push("null");
                    } else {
                        parents.push(key);
                    }
                }

                //if json object ends remove it from stack
                if (currentToken == JsonToken.END_OBJECT) {
                    parents.pop();
                }

                //found target node
                if (currentToken == JsonToken.FIELD_NAME && key.equals(TARGET_KEY)) {
                    //check hierarchy
                    final int size = parents.size();
                    if (size >= 3 &&
                            parents.get(size - 1).equals(RESOLVER_KEY) &&
                            parents.get(size - 2).equals(ISSUE_KEY) &&
                            parents.get(size - 3).equals(ISSUE_KEY)) {
                        //get next token for get value
                        final JsonToken jsonToken = parser.nextToken();
                        //if token is string value - add to targets
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
        } catch (IOException cause) {
            throw cause;
        }

        return targets;
    }

    /**
     * Creates a json parser which reads
     * given input stream.
     *
     * @param inputStream data to be parsed
     * @return initialized parser
     */
    private JsonParser initParser(InputStream inputStream) throws IOException {
        JsonFactory jsonfactory = new JsonFactory();
        try {
            return jsonfactory.createParser(inputStream);
        } catch (IOException cause) {
            throw cause;
        }
    }
}
