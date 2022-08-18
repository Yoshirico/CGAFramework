package MyArenaDefence

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Renderable
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import kotlin.system.exitProcess

class Player(private val objectpath : String, var radY : Float,val enemyList :  MutableList<Enemy>) {

    var currentRadius = radY

    var health = 2000
    val player : Renderable? = ModelLoader.loadModel(objectpath, Math.toRadians(0.0f), radY, 0.0f)
    var alive : Boolean = 0 < health

    init {
        if(player == null)
        {
            exitProcess(1)
        }

       // player.meshes[2].material.emitColor = Vector3f(1.0f, 0.0f, 0.0f)
        player.scaleLocal(Vector3f(1f))
        player.translateLocal(Vector3f(0.0f, 0.0f, -5.0f))
    }
    fun returnRadius(): Float = currentRadius

    fun takeDamage(damage : Int){ health -= damage }

    fun arenaborder (): Boolean{

        val playerX = player?.getWorldPosition();
        val playerY = player?.getWorldPosition();

        val x : Float = playerX!!.x;
        val z : Float= playerY!!.z;

        val squaredDis = Math.sqrt( x * x + z * z )

        if(squaredDis.toInt() > 15 ){
            return true
        }
        return false
    }

    fun playerWalking(x : Renderable?, dt : Float, window : GameWindow, cam : TronCamera){
        var speed = 0f
        var turnaround = 0f
        var jump = 0f

        if (alive){
            var checkDings = false
            for (gegner in enemyList){
                checkDings = colision(gegner)
            }

            if(window.getKeyState(GLFW.GLFW_KEY_W)) {
                if (arenaborder()){
                    player?.translateLocal(Vector3f(0f,0f,10f*dt))
                } else {
                    speed = -5.0f
                    if (window.getKeyState(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                        speed = -15.0f
                    }
                }
            }

            if(window.getKeyState(GLFW.GLFW_KEY_S)) {
                if (arenaborder()){
                    player?.translateLocal(Vector3f(0f,0f,-10f*dt))
                } else {
                    speed = 5.0f
                }
            }

            if(window.getKeyState(GLFW.GLFW_KEY_A)) {
                if (arenaborder()){
                    player?.translateLocal(Vector3f(10f*dt,0f,0f))
                } else {
                    turnaround = -5.0f
                }
            }

            if(window.getKeyState(GLFW.GLFW_KEY_D)) {
                if (arenaborder()){
                    player?.translateLocal(Vector3f(-10f*dt,0f,0f))
                } else {
                    turnaround = 5.0f
                }
            }

            x?.translateLocal(Vector3f(turnaround * dt, 0.0f, speed * dt))



        }
    }
    fun arena_border (): Boolean{

        val playerX = player?.getWorldPosition();
        val playerY = player?.getWorldPosition();

        val x : Float = playerX!!.x;
        val z : Float= playerY!!.z;

        val squaredDis = Math.sqrt( x * x + z * z )

        if(squaredDis.toInt() > 15 ){
            return true
        }
        return false
    }
    fun distanceToSomething(player : Renderable? , enemy : Enemy): Pair<Float,Float>{

        val playerX = player?.getWorldPosition();
        val playerY = player?.getWorldPosition();

        val px : Float = playerX!!.x;
        val pz : Float= playerY!!.z;

        val enemyX = enemy.enemy?.getWorldPosition();
        val enemyY = enemy.enemy?.getWorldPosition();

        val ex = enemyX!!.x;
        val ez = enemyY!!.z;

        val xDistance = px - ex;
        val zDistance = pz - ez;

        return Pair(xDistance,zDistance)
    }
    fun boxing(obj : Renderable?): MutableList<Float>{

        val playerx = obj?.getWorldPosition();
        val playerz = obj?.getWorldPosition();

        val lx : Float = playerx!!.x;
        val lz : Float= playerz!!.z;

        val posX = lx + 1.5f // 0
        val posY = lz + 1.5f // 1
        val negX = lx - 1.5f // 2
        val negY = lz - 1.5f // 3

        // println(p1); // <-- mittelpunkt der objekte

        return  mutableListOf<Float>(posX, posY, negX, negY,lx,lz);
    }
    fun colision(enemy : Enemy):Boolean{

        val playerBox = boxing(player);
        val enemyBox = boxing(enemy.enemy);
        val distance = distanceToSomething(player,enemy)

        val px = playerBox[4]
        val pz = playerBox[5]

        if(px < enemyBox[0] && px > enemyBox[2] && pz < enemyBox[1] && pz > enemyBox[3]){
            if (distance.first > distance.second){
                // X
                if(pz > enemyBox[3] && pz < (enemyBox[3] + 1.5f)){
                    return true
                }
                if (px < enemyBox[0] && px > (enemyBox[0] - 1.5f)){
                    return true
                }
            } else {
                //Z
                if(pz < enemyBox[1] && pz > (enemyBox[1] - 1.5f)){
                    return true
                }
                if(px > enemyBox[2] && px < (enemyBox[2] + 1.5f)){
                    return true
                }
            }
        }
        return false
    }




}