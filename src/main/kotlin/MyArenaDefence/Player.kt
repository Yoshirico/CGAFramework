package MyArenaDefence

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Math
import org.joml.Vector3f
import kotlin.system.exitProcess

class Player(private val objectpath : String, var radY : Float) {

    var currentRadius = radY

    var health = 2000
    val player : Renderable? = ModelLoader.loadModel(objectpath, Math.toRadians(0.0f), radY, 0.0f)

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



}