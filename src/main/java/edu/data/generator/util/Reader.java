package edu.data.generator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reader {

    private final static Logger LOG = LoggerFactory.getLogger(Reader.class);

    public static List<File> listFilesFromDir(final String directory) {
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            LOG.error("Error reading directory: {0}! Please make sure that the path is valid.", directory);
            return Collections.EMPTY_LIST;
        }
    }

}
