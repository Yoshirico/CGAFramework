package gui

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import renderEngine.Texture
import java.nio.FloatBuffer
import java.nio.IntBuffer
import org.lwjgl.opengl.GL11.*
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.imageio.ImageIO


class RawMeat(vaoID : Int, vertexCounter : Int){

    private val vaoID = vaoID
    private val vertexCounter = vertexCounter

    fun getVaoID():Int{
        return vaoID
    }

    fun getVertrxCount(): Int {
        return vertexCounter
    }
}

class Loader {

    private val vaos : MutableList<Int> = mutableListOf<Int>()
    private val vbos : MutableList<Int> = mutableListOf<Int>()

    fun loadToVAO(positions : FloatArray, indicies : IntArray): RawMeat{
        var vaoID : Int = createVAO()
        storeDataInAttributeList(0,positions)
        unbindVAO()
        return RawMeat(vaoID,indicies.size)
    }

    fun createVAO(): Int {
        val vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID)
        return vaoID
    }

    fun cleanUp(){
        for (i in vaos){
            GL30.glDeleteVertexArrays(i)
        }
        for (i in vbos){
            GL30.glDeleteVertexArrays(i)
        }
    }

    fun loadeTexture(filename : String){
        var texture : Texture


    }

    fun storeDataInFloatBuffer(data : FloatArray): FloatBuffer{
        var buffer : FloatBuffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    fun storeDataInAttributeList(attributNumber : Int, data : FloatArray){
        var vboID : Int = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        var buffer : FloatBuffer = storeDataInFloatBuffer(data)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(attributNumber,3, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    fun unbindVAO(){
        GL30.glBindVertexArray(0)
    }

    fun bindIndicesBuffer(indices: IntArray){
        var vboID = GL15.glGenBuffers()
        vbos.add(vboID)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID)
        var buffer : IntBuffer = intBuffer(indices)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
    }

    fun intBuffer(data : IntArray):IntBuffer{
        val buffer : IntBuffer = BufferUtils.createIntBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }
/*
    fun loadToVAO(pos : Float):RawMeat{
        var vaoID : Int = createVAO()
        this.storeDataInAttributeList()
        return
    }
 */

}