package ca.jonathanfritz.aoc2020;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    public static List<String> splitIntoChars(String s) {
        return Arrays.stream(s.split("(?!^)")).collect(Collectors.toList());
    }

    public static Stream<String> loadFromFile(String filename) {
        try {
            final Path path = Path.of(getResource(filename, Utils.class).toURI());
            return Files.lines(path);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to load file", e);
        }
    }

    // stolen from https://github.com/krosenvold/struts2/blob/master/xwork-core/src/main/java/com/opensymphony/xwork2/util/ClassLoaderUtil.java
    private static URL getResource(String resourceName, Class callingClass) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);

        if (url == null) {
            url = Utils.class.getClassLoader().getResource(resourceName);
        }

        if (url == null) {
            ClassLoader cl = callingClass.getClassLoader();

            if (cl != null) {
                url = cl.getResource(resourceName);
            }
        }

        if ((url == null) && (resourceName != null) && ((resourceName.length() == 0) || (resourceName.charAt(0) != '/'))) {
            return getResource('/' + resourceName, callingClass);
        }

        return url;
    }
}
