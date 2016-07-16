package com.alekseyzhelo.lbm.gui.lwjgl.render

import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.gui.lwjgl.cli.CMSettings
import com.alekseyzhelo.lbm.gui.lwjgl.render.mesh.MutableColourMesh
import com.alekseyzhelo.lbm.gui.lwjgl.render.shader.ShaderProgram
import com.alekseyzhelo.lbm.gui.lwjgl.util.ResourcesUtil
import com.alekseyzhelo.lbm.statistics.LatticeStatistics
import com.alekseyzhelo.lbm.util.norm
import com.alekseyzhelo.lbm.util.normalize
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL30.glBindVertexArray

/**
 * @author Aleks on 27-06-2016.
 */

class GL30Renderer(
        cli: CLISettings,
        cm: CMSettings,
        lattice: LatticeD2,
        WIDTH: Int = 750,
        HEIGHT: Int = 750
) : GLRenderer(cli, cm, lattice, WIDTH, HEIGHT) {

    private var shaderProgram: ShaderProgram? = null
    private var mesh: MutableColourMesh? = null

    override fun extraTerminate() {
        shaderProgram?.cleanup()
        mesh?.cleanUp()
    }

    override fun doInit() {
        super.doInit()

        shaderProgram = ShaderProgram()
        shaderProgram!!.createVertexShader(ResourcesUtil.loadResource("/vertex.vs"))
        shaderProgram!!.createFragmentShader(ResourcesUtil.loadResource("/fragment.fs"))
        shaderProgram!!.link()

        val vertices = BufferUtils.createFloatBuffer(cli.lx * cli.ly * 4 * 3)
        val indices = BufferUtils.createIntBuffer(cli.lx * cli.ly * 6)
        val colours = BufferUtils.createFloatBuffer(cli.lx * cli.ly * 4 * 3)

        val projectionMatrix = Matrix4f().ortho(0.0f, cli.lx.toFloat(), 0.0f, cli.ly.toFloat(), 1.0f, -1.0f)
        var vertexTmp = Vector4f()

        for (i in 0..cli.lx - 1) {
            for (j in 0..cli.ly - 1) {
                vertexTmp.x = i.toFloat() // left top
                vertexTmp.y = j + 1.0f
                vertexTmp.z = 0.0f
                vertexTmp.w = 1.0f
                vertexTmp = vertexTmp.mul(projectionMatrix)
                vertices.put(vertexTmp.x)
                vertices.put(vertexTmp.y)
                vertices.put(vertexTmp.z)

                colours.put(1.0f)
                colours.put(1.0f)
                colours.put(1.0f)

                vertexTmp.x = i.toFloat()  // left bottom
                vertexTmp.y = j.toFloat()
                vertexTmp.z = 0.0f
                vertexTmp.w = 1.0f
                vertexTmp = vertexTmp.mul(projectionMatrix)
                vertices.put(vertexTmp.x)
                vertices.put(vertexTmp.y)
                vertices.put(vertexTmp.z)

                colours.put(1.0f)
                colours.put(0.0f)
                colours.put(1.0f)

                vertexTmp.x = i + 1f  // right bottom
                vertexTmp.y = j.toFloat()
                vertexTmp.z = 0.0f
                vertexTmp.w = 1.0f
                vertexTmp = vertexTmp.mul(projectionMatrix)
                vertices.put(vertexTmp.x)
                vertices.put(vertexTmp.y)
                vertices.put(vertexTmp.z)

                colours.put(1.0f)
                colours.put(0.0f)
                colours.put(0.0f)

                vertexTmp.x = i + 1f  // right top
                vertexTmp.y = j + 1.0f
                vertexTmp.z = 0.0f
                vertexTmp.w = 1.0f
                vertexTmp = vertexTmp.mul(projectionMatrix)
                vertices.put(vertexTmp.x)
                vertices.put(vertexTmp.y)
                vertices.put(vertexTmp.z)

                colours.put(0.0f)
                colours.put(0.0f)
                colours.put(1.0f)
            }
        }
        vertices.flip()
        colours.flip()

        for (k in 0..cli.lx * cli.ly - 1) {
            val i = k * 4
            indices.put(i)
            indices.put(i + 1)
            indices.put(i + 3)
            indices.put(i + 3)
            indices.put(i + 1)
            indices.put(i + 2)

        }
        indices.flip()

        mesh = MutableColourMesh(vertices, colours, indices)
    }

    override fun doFrame(cells: Array<Array<CellD2Q9>>) {
        /* Get width and height to calculate the ratio */
        glfwGetFramebufferSize(window, width, height)

        /* Set viewport and clear screen */
        glViewport(0, 0, width.get(), height.get())
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        mesh?.updateColourBuffer {
            colors ->
            for (i in cells.indices) {
                for (j in cells[0].indices) {
                    val value = cellValue(cells[i][j])
                    val color = colormap.getColor(normalize(value, minValue(), maxValue()).toFloat())

                    colors.put(color.r)
                    colors.put(color.g)
                    colors.put(color.b)
                    colors.put(color.r)
                    colors.put(color.g)
                    colors.put(color.b)
                    colors.put(color.r)
                    colors.put(color.g)
                    colors.put(color.b)
                    colors.put(color.r)
                    colors.put(color.g)
                    colors.put(color.b)
                }
            }
            colors.flip()
        }

        /* Render  mesh */
        shaderProgram?.bind()

        // Draw the mesh
        glBindVertexArray(mesh!!.vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glDrawElements(GL_TRIANGLES, mesh!!.vertexCount, GL_UNSIGNED_INT, 0)

        // Restore state
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)

        shaderProgram?.unbind();
        /* Swap buffers and poll Events */
        glfwSwapBuffers(window)
        glfwPollEvents()

        /* Flip buffers for next loop */
        width.flip()
        height.flip()
    }

}