package com.example.demo.servises;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Thealeshka on 05.11.2019 inside the package - com.telegram.bot.servise
 */


public final class PreStart {
    public final static void createDirectories() {
        createDirectory("config");
        createDirectory("data");
        createFile("config/instruction.txt");
        createFile("config/startSemester.txt");
        createFile("config/botToken.txt");
    }

    private final static void createFile(String fileName) {
        if (!Files.exists(Path.of(fileName))) {
            try {
                Files.createFile(Path.of(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final static void createDirectory(String fileName) {
        if (!Files.exists(Path.of(fileName))) {
            try {
                Files.createDirectory(Path.of(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
