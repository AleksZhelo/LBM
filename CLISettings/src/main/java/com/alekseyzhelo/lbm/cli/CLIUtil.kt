package com.alekseyzhelo.lbm.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException

/**
 * @author Aleks on 29-05-2016.
 */
fun collectArguments(programName: String, args: Array<String>): CLISettings {
    val cli = CLISettings();

    val commander = JCommander(cli);
    commander.setProgramName(programName);

    try {
        commander.parse(*args)
    } catch (e: ParameterException) {
        val builder = StringBuilder()
        builder.appendln(e.message)
        commander.usage(builder)
        print(builder.toString())
        System.exit(1)
    } finally {
        return cli
    }
}
