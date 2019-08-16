package parser;

import exception.NotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParserTest {

    private Parser parser = new Parser();

    private List<String> hierarchy;

    @Before
    public void Before() {
        hierarchy = new ArrayList<>();
        hierarchy.add("issue");
        hierarchy.add("issue");
        hierarchy.add("resolver");
        hierarchy.add("value");
    }

    @Test
    public void TestParseTree() throws NotFoundException {
        final List<String> targets = parser.ParseJson("tree.json");
        Assert.assertEquals(targets.size(), 5);
        Assert.assertTrue(targets.contains("target1"));
        Assert.assertTrue(targets.contains("target3"));
        Assert.assertTrue(targets.contains("target5"));
        Assert.assertTrue(targets.contains("target6"));
        Assert.assertTrue(targets.contains("target8"));
    }

    @Test(expected = NotFoundException.class)
    public void TestParseWhenFileNotFound() throws NotFoundException {
        final List<String> targets = parser.ParseJson("not_found.json");
    }

    @Test
    public void TestParseWhenFileIsEmpty() throws NotFoundException {
        final List<String> targets = parser.ParseJson("empty.json");
        Assert.assertEquals(targets.size(), 0);
    }

    @Test
    public void TestParseWhenValueInArray() throws NotFoundException {
        final List<String> targets = parser.ParseJson("in_array.json");
        Assert.assertTrue(targets.contains("target3"));
        Assert.assertTrue(targets.contains("target4"));
    }

}
