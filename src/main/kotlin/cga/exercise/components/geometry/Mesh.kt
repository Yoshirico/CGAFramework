package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */

interface IMaterial {
    fun bind(shaderProgram: ShaderProgram)

    fun cleanup()
}
class Mesh(vertexdata: FloatArray, indexdata: IntArray, attributes: Array<VertexAttribute>, val material: IMaterial?) {
    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount = 0

    init {

        indexcount = indexdata.size

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo = GL30.glGenBuffers()
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexdata, GL30.GL_STATIC_DRAW)

        ibo = GL30.glGenBuffers()
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo)
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indexdata, GL30.GL_STATIC_DRAW)

        for(i in 0 until attributes.size){
            GL30.glEnableVertexAttribArray(i);
            GL30.glVertexAttribPointer(i, attributes.size, attributes[i].type, false, attributes[i].stride, attributes[i].offset.toLong())
        }

        GL30.glBindVertexArray(0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    fun render() {
        GL30.glBindVertexArray(vao)
        GL11.glDrawElements(GL11.GL_TRIANGLES, indexcount, GL11.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)

    }

    fun render(shaderProgram : ShaderProgram) {
        shaderProgram.use()
        material?.bind(shaderProgram)
        render()
    }

    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}