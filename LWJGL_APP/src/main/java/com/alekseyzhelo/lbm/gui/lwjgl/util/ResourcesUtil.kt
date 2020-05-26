package com.alekseyzhelo.lbm.gui.lwjgl.util

import com.opencsv.CSVReader
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import javax.imageio.ImageIO

object ResourcesUtil {
    @Throws(Exception::class)
    fun loadResource(fileName: String): String {
        return ResourcesUtil::class.java.getResourceAsStream(fileName)
            .use {
                Scanner(it, "UTF-8").useDelimiter("\\A").next()
            }
    }

    @Throws(Exception::class)
    fun loadCSVResource(fileName: String): List<Array<String>> {
        val reader =
            CSVReader(InputStreamReader(ResourcesUtil::class.java.getResourceAsStream(fileName)))
        return reader.readAll()
    }

    @Throws(IOException::class)
    fun loadImageResource(fileName: String): BufferedImage {
        // using this stream the image is loaded asynchronously
        //InputStream stream = ResourcesUtil.class.getResourceAsStream(fileName);
        return ImageIO.read(ResourcesUtil::class.java.getResource(fileName))
    }
}