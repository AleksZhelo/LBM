package com.alekseyzhelo.lbm.cli;

import com.beust.jcommander.Parameter;

public class LatticeFileSettings extends CLISettings {

    @Parameter(names = {"--lattice-file", "-file"}, description = "Lattice image file (.bmp).", required = true)
    private String latticeFile = null;

    public String getLatticeFile() {
        return latticeFile;
    }
}
