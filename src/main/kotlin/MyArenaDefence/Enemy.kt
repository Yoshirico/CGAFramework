package MyArenaDefence

import cga.framework.ModelLoader
import org.joml.Math
import kotlin.system.exitProcess
import cga.exercise.components.geometry.Renderable
import org.joml.*

class Enemy(private val objectpath : String, var radY : Float) {

    var currentRadius = radY

    var damage = 1
    val enemy : Renderable? = ModelLoader.loadModel(objectpath, Math.toRadians(-90.0f), radY, 0.0f)

    init {
        if(enemy == null)
        {
            exitProcess(1)
        }

        enemy.meshes[2].material.emitColor = Vector3f(1.0f, 0.0f, 0.0f)
        enemy.scaleLocal(Vector3f(0.5f))
        enemy.translateLocal(Vector3f(0.0f, 0.0f, -15.0f))
    }

    fun returnRadius(): Float = currentRadius


}