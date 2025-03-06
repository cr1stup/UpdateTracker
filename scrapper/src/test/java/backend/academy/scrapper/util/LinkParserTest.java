package backend.academy.scrapper.util;

import java.net.URI;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LinkParserTest {

    @Test
    @DisplayName("parser recognizes valid github link")
    void testGithubValidLink() {
        Pattern pattern = Pattern.compile("https://github.com/([^/]+)/([^/]+)");
        URI url = URI.create("https://github.com/username/repository");

        Assertions.assertTrue(LinkParser.isSupport(url, pattern));
    }

    @Test
    @DisplayName("parser recognizes invalid github link")
    void testGithubInvalidLink() {
        Pattern pattern = Pattern.compile("https://github.com/([^/]+)/([^/]+)");
        URI url = URI.create("https://github.com/repository");

        Assertions.assertFalse(LinkParser.isSupport(url, pattern));
    }

    @Test
    @DisplayName("parser recognizes valid stackoverflow link")
    void testSOValidLink() {
        Pattern pattern = Pattern.compile("https://stackoverflow.com/questions/(\\d+).*");
        URI url = URI.create("https://stackoverflow.com/questions/79485942/test");

        Assertions.assertTrue(LinkParser.isSupport(url, pattern));
    }

    @Test
    @DisplayName("parser recognizes invalid stackoverflow link")
    void testSOInvalidLink() {
        Pattern pattern = Pattern.compile("https://stackoverflow.com/questions/(\\d+).*");
        URI url = URI.create("https://stackorflow.com/questions/79485942/test");

        Assertions.assertFalse(LinkParser.isSupport(url, pattern));
    }

    @Test
    @DisplayName("parser returns correct question id")
    void testGetQuestionId() {
        Pattern pattern = Pattern.compile("https://stackoverflow.com/questions/(\\d+).*");
        URI url = URI.create("https://stackoverflow.com/questions/79485942/test");

        String id = LinkParser.getQuestionId(url, pattern);

        Assertions.assertEquals("79485942", id);
    }
}
