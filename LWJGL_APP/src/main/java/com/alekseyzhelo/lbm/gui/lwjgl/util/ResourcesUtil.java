package com.alekseyzhelo.lbm.gui.lwjgl.util;

import com.opencsv.CSVReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class ResourcesUtil {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = ResourcesUtil.class.getResourceAsStream(fileName)) {
            result = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String[]> loadCSVResource(String fileName) throws Exception {
        CSVReader reader = new CSVReader(new InputStreamReader(ResourcesUtil.class.getResourceAsStream(fileName)));
        return reader.readAll();
    }

    public static BufferedImage loadImageResource(String fileName) throws IOException {
        // using this stream the image is loaded asynchronously
        //InputStream stream = ResourcesUtil.class.getResourceAsStream(fileName);
        return ImageIO.read(ResourcesUtil.class.getResource(fileName));
    }

}