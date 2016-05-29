package com.alekseyzhelo.lbm.cli;

import com.beust.jcommander.Parameter;

/**
 * @author Aleks on 21-02-2016.
 */
public class CLISettings {

    //@Parameter(description = "timetable file")
    //private List<String> files = new ArrayList<String>();

    @Parameter(names = {"--time", "-t"}, description = "Simulation time steps", required = true)
    private Integer time = null;

    @Parameter(names = {"--length-x", "-lx"}, description = "Simulation rectangle X size (nodes).", required = true)
    private Integer lx = null;

    @Parameter(names = {"--length-y", "-ly"}, description = "Simulation rectangle Y size (nodes).", required = true)
    private Integer ly = null;

    @Parameter(names = {"--omega", "-o"}, description = "Reciprocal value of the relaxation parameter (1/tau).", required = true)
    private Double omega = null;

    @Parameter(names = {"--draw-velocities", "-vel"}, description = "Will draw velocities (instead of pressures).")
    private Boolean drawVelocities = false;

    @Parameter(names = {"--stopping", "-s"}, description = "Wait for any key input after every iteration.")
    private Boolean stopping = false;

    @Parameter(names = {"--verbose", "-v"}, description = "Print step-by-step details.")
    private Boolean verbose = false;

    @Parameter(names = {"--no-collision", "-nc"}, description = "Do not calculate collisions.")
    private Boolean noCollisions = false;

    public Integer getTime() {
        return time;
    }

    public Double getOmega() {
        return omega;
    }

    public Integer getLx() {
        return lx;
    }

    public Integer getLy() {
        return ly;
    }

    public Boolean getDrawVelocities() {
        return drawVelocities;
    }

    public Boolean getStopping() {
        return stopping;
    }

    public Boolean getVerbose() {
        return verbose;
    }

    public Boolean getNoCollisions() {
        return noCollisions;
    }
}
