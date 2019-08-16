package parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParserTest {

    private Parser parser = new Parser();

    private List<String> hierarchy;

    @Before
    public void before() {
        hierarchy = new ArrayList<>();
        hierarchy.add("issue");
        hierarchy.add("issue");
        hierarchy.add("resolver");
        hierarchy.add("value");
    }

    @Test
    public void testParseTree() throws IOException {
        final List<String> targets = parser.parseJson(readFile("tree.json"));
        Assert.assertEquals(targets.size(), 5);
        Assert.assertTrue(targets.contains("target1"));
        Assert.assertTrue(targets.contains("target3"));
        Assert.assertTrue(targets.contains("target5"));
        Assert.assertTrue(targets.contains("target6"));
        Assert.assertTrue(targets.contains("target8"));
    }

    @Test
    public void testParseWhenFileIsEmpty() throws IOException {
        final List<String> targets = parser.parseJson(readFile("empty.json"));
        Assert.assertEquals(targets.size(), 0);
    }

    @Test
    public void testParseWhenValueInArray() throws IOException {
        final List<String> targets = parser.parseJson(readFile("in_array.json"));
        Assert.assertTrue(targets.contains("target3"));
        Assert.assertTrue(targets.contains("target4"));
    }

    private InputStream readFile(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final InputStream resourceAsStream = classloader.getResourceAsStream(filename);
        if (resourceAsStream == null) {
            throw new RuntimeException("File not found");
        }

        return resourceAsStream;
    }

}
