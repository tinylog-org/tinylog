package org.tinylog.impl.path.segments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.OptionalLong;

/**
 * Path segment for a sequential count.
 */
public class CountSegment implements PathSegment {

    /** */
    public CountSegment() {
    }

    @Override
    public String findLatest(Path parentDirectory, String prefix) throws IOException {
        OptionalLong maxCount = Files.list(parentDirectory)
            .map(path -> path.getFileName().toString())
            .filter(name -> name.startsWith(prefix))
            .mapToLong(name -> getCount(name, prefix))
            .filter(number -> number >= 0)
            .max();

        if (maxCount.isPresent()) {
            return Long.toString(maxCount.getAsLong());
        } else {
            return null;
        }
    }

    @Override
    public void resolve(StringBuilder pathBuilder, ZonedDateTime date) throws IOException {
        // Add "_" as dummy to avoid getting the parent directory if path builder ends with a separator like "/" or "\"
        Path expandedPath = Paths.get(pathBuilder + "_");
        Path parentPath = expandedPath.getParent();

        String expandedName = expandedPath.getFileName().toString();
        String namePrefix = expandedName.substring(0, expandedName.length() - 1); // Without the appended dummy "_"

        long currentMax;
        try {
            currentMax = Files.list(parentPath)
                .map(path -> path.getFileName().toString())
                .filter(name -> name.startsWith(namePrefix))
                .mapToLong(name -> getCount(name, namePrefix))
                .max()
                .orElse(-1);
        } catch (NoSuchFileException ex) {
            currentMax = -1;
        }

        if (currentMax < 0 || currentMax == Long.MAX_VALUE) {
            pathBuilder.append('0');
        } else {
            pathBuilder.append(currentMax + 1);
        }
    }

    /**
     * Extracts the sequential count from a file name.
     *
     * @param fileName The file name
     * @param prefix The plain static prefix before the sequential count
     * @return The extracted count or {@code -1} if the file name doesn't contain a valid numeric count
     */
    private static long getCount(String fileName, String prefix) {
        int start = prefix.length();
        int end = prefix.length();

        while (end < fileName.length() && isDigit(fileName.charAt(end))) {
            end += 1;
        }

        try {
            String count = fileName.substring(start, end);
            return Long.parseLong(count);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    /**
     * Tests if a character is an ASCII digit ('0' - '9').
     *
     * @param character The character to test
     * @return {@code true} if the character is in a digit, otherwise {@code false}
     */
    private static boolean isDigit(char character) {
        return character >= '0' && character <= '9';
    }

}
