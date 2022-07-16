package MyArenaDefence

import cga.framework.ModelLoader
import org.joml.Math
import kotlin.system.exitProcess
import cga.exercise.components.geometry.Renderable
import org.joml.*

class Enemy(private val objectpath : String, var radY : Float) {

    var currentRadius = radY

    var damage = 10
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

    fun enemyLogic( player : Renderable? , dt : Float, dist : Pair<Float,Float>){

        if (dist.first > 0.1f && dist.first != 0f){
            enemy?.translateLocal(Vector3f(2f * dt, 0f, 0f * dt))
        } else if (dist.first < 1f && dist.first != 0f){
            enemy?.translateLocal(Vector3f(-2f * dt, 0f, 0f * dt))
        }

        if (dist.second > 0.1f && dist.second != 0f) {
            enemy?.translateLocal(Vector3f(0f * dt, 0f, 2f * dt))
        } else if (dist.second < 1f && dist.second != 0f){
            enemy?.translateLocal(Vector3f(0f * dt, 0f, -2f * dt))
        }
    }


}