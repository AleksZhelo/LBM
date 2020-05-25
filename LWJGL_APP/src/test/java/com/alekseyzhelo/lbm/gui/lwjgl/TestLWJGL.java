package com.alekseyzhelo.lbm.gui.lwjgl;

import org.junit.Test;
import org.lwjgl.Version;

/**
 * @author Aleks on 27-06-2016.
 */

public class TestLWJGL {

    @Test
    public void TestVersion(){
        System.out.println("LWJGL Version " + Version.getVersion() + " is working.");
    }

}