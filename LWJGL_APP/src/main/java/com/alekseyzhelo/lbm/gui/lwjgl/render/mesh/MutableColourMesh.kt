package com.alekseyzhelo.lbm.gui.lwjgl.render.mesh

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * @author Aleks on 28-06-2016.
 */
class MutableColourMesh(posBuffer: FloatBuffer, colourBuffer: FloatBuffer, indicesBuffer: IntBuffer) {

    val vaoId: Int

    private val posVboId: Int

    val colourVboId: Int

    private val idxVboId: Int

    val vertexCount: Int
    val colourCount: Int

    init {
        vertexCount = indicesBuffer.capacity()
        colourCount = colourBuffer.capacity() * 4

        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        // Position VBO
        posVboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, posVboId)
        glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        // Colour VBO
        colourVboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, colourVboId)
        glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_DYNAMIC_DRAW) // TODO: proper use of dynamic draw?
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)

        // Index VBO
        idxVboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    inline fun updateColourBuffer(update: (buf: FloatBuffer) -> Unit) {
        glBindBuffer(GL_ARRAY_BUFFER, colourVboId)
        val mappedBuffer = glMapBufferRange(
                GL_ARRAY_BUFFER, 0, colourCount.toLong(),
                GL_MAP_WRITE_BIT or GL_MAP_INVALIDATE_BUFFER_BIT).asFloatBuffer()

        update(mappedBuffer)

        glUnmapBuffer(GL_ARRAY_BUFFER)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun cleanUp() {
        glDisableVertexAttribArray(0)

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(posVboId)
        glDeleteBuffers(colourVboId)
        glDeleteBuffers(idxVboId)

        // Delete the VAO
        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }
}

