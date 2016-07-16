package com.alekseyzhelo.lbm.gui.lwjgl.render

import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2
import com.alekseyzhelo.lbm.gui.lwjgl.cli.CMSettings
import com.alekseyzhelo.lbm.gui.lwjgl.color.colormap.*
import com.alekseyzhelo.lbm.statistics.LatticeStatistics
import com.alekseyzhelo.lbm.util.norm
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil

/**
 * @author Aleks on 03-07-2016.
 */
abstract class GLRenderer(
        val cli: CLISettings,
        val cm: CMSettings,
        lattice: LatticeD2,
        val WIDTH: Int = 750,
        val HEIGHT: Int = 750
) {

    // The window handle
    protected var window: Long = 0

    protected val width = BufferUtils.createIntBuffer(1)
    protected val height = BufferUtils.createIntBuffer(1)

    protected val colormap: Colormap

    protected val minValue: () -> Double
    protected val maxValue: () -> Double
    protected val cellValue: (cell: CellD2Q9) -> Double

    init {
        colormap = resolveColormap()

        if (cli.noRescale) {
            LatticeStatistics.configure(false, false)
        } else {
            LatticeStatistics.configure(!cli.drawVelocities, cli.drawVelocities)
        }

        if (cli.drawVelocities) {
            minValue = { 0.0 }
            maxValue = { LatticeStatistics.maxVelocity }
            cellValue = { cell: CellD2Q9 -> norm(cell.U) } // TODO: indicate that we are one iteration behind like this
        } else {
            minValue = { LatticeStatistics.minDensity }
            maxValue = { LatticeStatistics.maxDensity }
            cellValue = { cell: CellD2Q9 -> cell.computeRho() }
        }
    }

    val frame: (cells: Array<Array<CellD2Q9>>) -> Unit = if (cli.headless) { x -> Unit } else { x -> doFrame(x) }

    fun initialize() {
        if (cli.headless) {
            return
        }

        doInit()
    }

    fun terminate() {
        if (cli.headless) {
            return
        }

        try {
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window)
            glfwDestroyWindow(window)

            extraTerminate()
        } finally {
            // Terminate GLFW and free the error callback
            glfwTerminate()
            glfwSetErrorCallback(null).free()
        }
    }

    protected open fun extraTerminate() {
    }

    protected open fun doInit() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        // Configure our window
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL)
            throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window, key, scanCode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true) // We will detect this in our rendering loop
        }

        // Get the resolution of the primary monitor
        val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        // Center our window
        glfwSetWindowPos(
                window,
                (vidMode.width() - WIDTH) / 2,
                (vidMode.height() - HEIGHT) / 2)

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    }

    fun windowShouldClose() = if (cli.headless) false else glfwWindowShouldClose(window)

    abstract protected fun doFrame(cells: Array<Array<CellD2Q9>>)

    private fun resolveColormap(): Colormap {
        // TODO: automatically list all implemented colormaps?
        return when (cm.colormap) {
            CoolwarmDiscreteColormap.getName() -> CoolwarmDiscreteColormap
            CoolwarmRGBInterpolatedColormap.getName() -> CoolwarmRGBInterpolatedColormap
            BluerJetColormap.getName() -> BluerJetColormap
            BlueRedColormap.getName() -> BlueRedColormap
            else -> CoolwarmDiscreteColormap
        }
    }

}