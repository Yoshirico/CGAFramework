package renderEngine

import gui.RawMeat
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Renderer {

    fun prepare(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClearColor(1f,0f,0f,1f)
    }

    fun render (model : RawMeat){
        GL30.glBindVertexArray(model.getVaoID())
        GL20.glEnableVertexAttribArray(0)
        GL11.glDrawArrays(GL11.GL_TRIANGLES,0 , model.getVertrxCount())
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
    }
}