package com.marosbrindza;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

public class JsonReader {

    // read all JSON files from a directory and return a list of their paths
    public List<Path> readJsonFiles(String directory) throws IOException {

        List<Path> jsonFiles = new ArrayList<>();

        Path folder = Paths.get(directory);

        // try to read all files in the directory and filter for JSON files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {

            for (Path file : stream) {
                if (file.toString().endsWith(".json")) {
                    jsonFiles.add(file);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading directory: " + directory, e);
        }
        return jsonFiles;
    }
}
