package com.marosbrindza;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

public class JsonReader {

    public List<Path> readJsonFiles(String directory) throws IOException {

        List<Path> jsonFiles = new ArrayList<>();

        Path folder = Paths.get(directory);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {

            for (Path file : stream) {
                if (file.toString().endsWith(".json")) {
                    jsonFiles.add(file);
                }
            }
        }
        return jsonFiles;
    }
}
