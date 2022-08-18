package MyArenaDefence


import cga.framework.ModelLoader
import org.joml.Math
import kotlin.system.exitProcess
import cga.exercise.components.geometry.Renderable
import org.joml.*


class Enemy(private val objectpath : String, var radY : Float) {
    var health = 1000
    var isOn = true
    var currentRadius = radY

    var damage = 10
    val enemy : Renderable? = ModelLoader.loadModel(objectpath, Math.toRadians(0.0f), radY, 0.0f)




    init {
        if(enemy == null)
        {
            exitProcess(1)
        }

        //enemy.meshes[2].material.emitColor = Vector3f(1.0f, 0.0f, 0.0f)
        enemy.scaleLocal(Vector3f(0.5f))
        enemy.translateLocal(Vector3f(0.0f, 0.0f, -15.0f))
    }

    fun returnRadius(): Float = currentRadius

    fun drive(dt : Float, speed : Float) = enemy?.translateLocal(Vector3f(speed * dt, 0f, 0f * dt))

    fun enemyLogic( player : Renderable? , dt : Float, dist : Pair<Float,Float>, deg: Float){

        if (radY < deg){
            enemy?.rotateAroundPoint(0f, -2f,0f ,enemy!!.getWorldPosition())
            radY += 2f
        } else if (radY >= deg){
            enemy?.rotateAroundPoint(0f, 2f,0f ,enemy!!.getWorldPosition())
            radY -= 2f
        }

    }


}