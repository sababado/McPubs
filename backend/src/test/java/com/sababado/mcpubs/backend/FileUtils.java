package com.sababado.mcpubs.backend;

import java.io.File;

/**
 * Created by robert on 8/27/16.
 */
public class FileUtils {
    public static File readFile(String fileName) {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        return new File(classLoader.getResource("" + fileName).getFile());
    }
}
