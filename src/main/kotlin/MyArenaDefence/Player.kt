package MyArenaDefence

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Renderable
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import kotlin.system.exitProcess

class Player(private val objectpath : String, var radY : Float) {

    var currentRadius = radY

    var health = 2000
    val player : Renderable? = ModelLoader.loadModel(objectpath, Math.toRadians(0.0f), radY, 0.0f)
    var alive : Boolean = 0 < health

    init {
        if(player == null)
        {
            exitProcess(1)
        }

        player.meshes[2].material.emitColor = Vector3f(1.0f, 0.0f, 0.0f)
        player.scaleLocal(Vector3f(1f))
        player.translateLocal(Vector3f(0.0f, 0.0f, -5.0f))
    }
    fun returnRadius(): Float = currentRadius

    fun takeDamage(damage : Int){ health -= damage }

    fun playerWalking(x : Renderable?, dt : Float, window : GameWindow, cam : TronCamera){
        var speed = 0f
        var turnaround = 0f
        var jump = 0f

        if (alive){

            if(window.getKeyState(GLFW.GLFW_KEY_W)) {
                speed = -5.0f
                if (window.getKeyState(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    speed = -15.0f
                }
            }

            if(window.getKeyState(GLFW.GLFW_KEY_S)) {
                speed = 5.0f
            }

            if(window.getKeyState(GLFW.GLFW_KEY_A)) {
                turnaround = -5f
            }
            else if(window.getKeyState(GLFW.GLFW_KEY_D)) {
                turnaround = 5f
            }

            x?.translateLocal(Vector3f(turnaround * dt, 0.0f, speed * dt))



        }
    }




}