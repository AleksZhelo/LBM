package com.alekseyzhelo.lbm.gui.lwjgl.cli;

import com.beust.jcommander.Parameter;

/**
 * @author Aleks on 03-07-2016.
 */
public class CMSettings {
    @Parameter(names = {"--colormap", "-cm"}, description = "The colormap to use in visualization.")
    private String colormap = "coolwarm";

    public String getColormap() {
        return colormap;
    }
}
