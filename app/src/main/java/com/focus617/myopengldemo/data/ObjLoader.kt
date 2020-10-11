package com.focus617.myopengldemo.data

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.Matrix
import android.util.Log
import com.focus617.myopengldemo.R
//import org.apache.commons.io.IOUtils
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.nio.charset.Charset
import java.util.*

// TODO: will remove after my ObjLoader ready

class Obj(context: Context) {
    private var verticesList = mutableListOf<String>()
    private var facesList = mutableListOf<String>()

    private lateinit var verticesBuffer: FloatBuffer
    private lateinit var facesBuffer: ShortBuffer
    private var program: Int? = null

    init {
        try {
            val scanner = Scanner(context.assets.open("torus.obj"))
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                if(line.startsWith("v")) {
                    verticesList.add(line)
                } else if (line.startsWith("f")) {
                    facesList.add(line)
                }
            }
            scanner.close()

            // Allocate buffers
            createBufferForVertices()
            createBufferForFaces()

            // Fill buffers
            fillVertices()
            fillFaces()

            // Convert vertex_shader.glsl to string
            val vertexShaderStream = context.resources.openRawResource(R.raw.simple_vertex_shader)
            //val vertexShaderSource = IOUtils.toString(vertexShaderStream, Charset.defaultCharset())
            vertexShaderStream.close()

            // Convert fragment_shader.glsl to string
            val fragmentShaderStream = context.resources.openRawResource(R.raw.simple_fragment_shader)
            //val fragmentShaderSource = IOUtils.toString(fragmentShaderStream, Charset.defaultCharset())
            fragmentShaderStream.close()

            // Create vertex shader
            val vertexShader = glCreateShader(GL_VERTEX_SHADER)
            //glShaderSource(vertexShader, vertexShaderSource)

            // Create fragment shader
            val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
            //glShaderSource(fragmentShader, fragmentShaderSource)

            // Compile shaders
            glCompileShader(vertexShader)
            glCompileShader(fragmentShader)

            // Create shader program
            program = glCreateProgram().also { program ->
                glAttachShader(program, vertexShader)
                glAttachShader(program, fragmentShader)

                glLinkProgram(program)
                glUseProgram(program)
            }

        } catch (ex: Exception) {
            Log.e("Obj", ex.message.toString())
        }


    }

    fun draw() {
        program?.let {program ->
            try {
                // Send vertex buffer to vertex shader
                val position = glGetAttribLocation(program, "position")
                glEnableVertexAttribArray(position)
                glVertexAttribPointer(position, 3, GL_FLOAT, false, 3 * 4, verticesBuffer)

                // Set camera position and look at
                val projectionMatrix = FloatArray(16)
                val viewMatrix = FloatArray(16)
                val productMatrix = FloatArray(16)

                Matrix.frustumM(
                    projectionMatrix, 0,
                    -1.0f, 1.0f,
                    -1.0f, 1.0f,
                    2.0f, 9.0f
                )

                Matrix.setLookAtM(
                    viewMatrix, 0,
                    0.0f, 3.0f, -4.0f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f
                )

                Matrix.multiplyMM(
                    productMatrix, 0,
                    projectionMatrix, 0,
                    viewMatrix, 0
                )


                val matrix = glGetUniformLocation(program, "matrix")
                glUniformMatrix4fv(matrix, 1, false, productMatrix, 0)

                // Use face list to create faces
                glDrawElements(GL_TRIANGLES, facesList.size * 3, GL_UNSIGNED_SHORT, facesBuffer)

                glDisableVertexAttribArray(position)
            } catch (ex: Exception) {
                println(ex.localizedMessage)
            }
        }
    }

    /**
     * Uses [facesList] which is a list of lines from OBJ containing face data
     * to create short buffer face list
     */
    private fun fillFaces() {
        for(face in facesList) {
            println(face)
            val vertexIndices = face.split(" ")

            val vertex1: Short = vertexIndices[1].toShort()
            val vertex2: Short = vertexIndices[2].toShort()
            val vertex3: Short = vertexIndices[3].toShort()

            facesBuffer.put((vertex1 - 1).toShort())
            facesBuffer.put((vertex2 - 1).toShort())
            facesBuffer.put((vertex3 - 1).toShort())
        }
        facesBuffer.position(0)

    }

    /**
     * Uses [verticesList] which is a list of lines from OBJ containing vertex data
     * to create float buffer vertex list
     */
    private fun fillVertices() {
        for (vertex in verticesList) {
            val coordinates = vertex.split(" ")
            val x =  coordinates[1].toFloat()
            val y =  coordinates[2].toFloat()
            val z =  coordinates[3].toFloat()
            verticesBuffer.put(x)
            verticesBuffer.put(y)
            verticesBuffer.put(z)
        }
        verticesBuffer.position(0)
    }

    /**
     * Initializes facesBuffer
     */
    private fun createBufferForFaces() {
        val buffer: ByteBuffer = ByteBuffer.allocateDirect(facesList.size * 3 * 2)
        buffer.order(ByteOrder.nativeOrder())
        facesBuffer = buffer.asShortBuffer()

    }

    /**
     * Initializes verticesBuffer
     */
    private fun createBufferForVertices() {
        val buffer: ByteBuffer = ByteBuffer.allocateDirect(verticesList.size * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())
        verticesBuffer = buffer.asFloatBuffer()
    }
}