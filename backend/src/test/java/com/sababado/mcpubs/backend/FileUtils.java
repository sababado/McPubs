package com.sababado.mcpubs.backend;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

/**
 * Created by robert on 8/27/16.
 */
public class FileUtils {
    public static File readFile(String fileName) {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }

    public static Document parseFile(String fileName) throws IOException {
        return Jsoup.parse(readFile(fileName), "UTF-8");
    }
}
