package com.alekseyzhelo.lbm.gui.lwjgl.util;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class ResourcesUtil {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = ResourcesUtil.class.getClass().getResourceAsStream(fileName)) {
            result = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String[]> loadCSVResource(String fileName) throws Exception {
        CSVReader reader = new CSVReader(new InputStreamReader(ResourcesUtil.class.getClass().getResourceAsStream(fileName)));
        return reader.readAll();
    }

}