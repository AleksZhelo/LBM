package com.alekseyzhelo.lbm.gui.lwjgl.render

import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.gui.lwjgl.cli.CMSettings
import com.alekseyzhelo.lbm.gui.lwjgl.render.GLRenderer
import com.alekseyzhelo.lbm.util.norm
import com.alekseyzhelo.lbm.util.normalize
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*

/**
 * @author Aleks on 27-06-2016.
 */

class GL11Renderer(
        cli: CLISettings,
        cm: CMSettings,
        lattice: LatticeD2,
        WIDTH: Int = 750,
        HEIGHT: Int = 750
) : GLRenderer(cli, cm, lattice, WIDTH, HEIGHT) {

    override fun doFrame(cells: Array<Array<CellD2Q9>>) {
        /* Get width and height to calculate the ratio */
        glfwGetFramebufferSize(window, width, height)

        /* Set viewport and clear screen */
        glViewport(0, 0, width.get(), height.get())
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        /* Set orthographic projection */
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0.0, cli.lx.toDouble(), 0.0, cli.ly.toDouble(), 1.0, -1.0)
        glMatrixMode(GL_MODELVIEW)

        /* Render  quads */
        glBegin(GL_QUADS)

        for (i in cells.indices) {
            for (j in cells[0].indices) {
                val value = cellValue(cells[i][j])
                val normalized = normalize(value, minValue(), maxValue())

                val color = colormap.getColor(normalized.toFloat())
                glColor3f(color.r, color.g, color.b)

                glVertex3i(i, j, 0) // left top
                glVertex3i(i + 1, j, 0) // left bottom
                glVertex3i(i + 1, j + 1, 0) // right top
                glVertex3i(i, j + 1, 0) // right bottom
            }
        }

        glEnd()

        /* Swap buffers and poll Events */
        glfwSwapBuffers(window)
        glfwPollEvents()

        /* Flip buffers for next loop */
        width.flip()
        height.flip()
    }


}