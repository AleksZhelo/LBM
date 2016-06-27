package com.alekseyzhelo.lbm.gui.lwjgl;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class is a simple quick starting guide. This is mainly a java conversion
 * of the
 * <a href=http://www.glfw.org/docs/latest/quick.html>Getting started guide</a>
 * from the official GLFW3 homepage.
 *
 * @author Heiko Brumme
 */
public class TestQuads {

    /**
     * This error callback will simply print the error to
     * <code>System.err</code>.
     */
    private static GLFWErrorCallback errorCallback
            = GLFWErrorCallback.createPrint(System.err);

    /**
     * The main function will create a 640x480 window and renders a rotating
     * triangle until the window gets closed.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long window;

        /* Set the error callback */
        glfwSetErrorCallback(errorCallback);

        /* Initialize GLFW */
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        /* Create window */
        window = glfwCreateWindow(640, 480, "Simple example", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        /* Center the window on screen */
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window,
                (vidMode.width() - 640) / 2,
                (vidMode.height() - 480) / 2
        );

        /* Create OpenGL context */
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        /* Enable vertical synchronization */
        glfwSwapInterval(1);

        /* Set the key callback */
// Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (wnd, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(wnd, true); // We will detect this in our rendering loop
        });

        /* Declare buffers for using inside the loop */
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);

        /* Loop until window gets closed */
        while (!glfwWindowShouldClose(window)) {
            float ratio;

            /* Get width and height to calculate the ratio */
            glfwGetFramebufferSize(window, width, height);
            ratio = width.get() / (float) height.get();

            /* Rewind buffers for next get */
            width.rewind();
            height.rewind();

            /* Set viewport and clear screen */
            glViewport(0, 0, width.get(), height.get());
            glClear(GL_COLOR_BUFFER_BIT);

            /* Set ortographic projection */
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(-ratio, ratio, -1f, 1f, 1f, -1f);
            glMatrixMode(GL_MODELVIEW);

            /* Render quad */
            // Clear the screen and depth buffer
            //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // set the color of the quad (R,G,B,A)
            glColor3f(0.5f,0.5f,1.0f);

            // draw quad
            glBegin(GL_QUADS);
            glVertex3f(0.1f,0.1f,0f);
            glVertex3f(0.1f+0.2f,0.1f,0f);
            glVertex3f(0.1f+0.2f,0.1f+0.2f,0f);
            glVertex3f(0.1f,0.1f+0.2f,0f);
            glEnd();

            /* Swap buffers and poll Events */
            glfwSwapBuffers(window);
            glfwPollEvents();

            /* Flip buffers for next loop */
            width.flip();
            height.flip();
        }

        /* Release window and its callbacks */
        glfwDestroyWindow(window);

        /* Terminate GLFW and release the error callback */
        glfwTerminate();
        errorCallback.free();
    }

}