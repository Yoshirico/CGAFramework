package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import cga.framework.OBJLoader.OBJMesh
import cga.framework.OBJLoader.OBJResult
import org.joml.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import kotlin.math.acos
import kotlin.math.sqrt
import kotlin.system.exitProcess


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram

    private val cam : TronCamera

    private var ground : Renderable
    var lightCycle : Renderable?
    var enemyCycle : Renderable?

    private var licht1 : PointLight
    private var licht2 : PointLight
    private var licht3 : PointLight

    private var pointLight : PointLight
    private var spotLight : SpotLight


    //scene setup
    init {

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()

        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        //Create the mesh
        val stride: Int = 8 * 4
        val attrPos = VertexAttribute(3, GL_FLOAT, stride, 0) //position
        val attrTC = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //textureCoordinate
        val attrNorm = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normalval
        val vertexAttributes = arrayOf<VertexAttribute>(attrPos, attrTC, attrNorm)


        lightCycle = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f), Math.toRadians(90.0f), 0.0f)
        if(lightCycle == null)
        {
            exitProcess(1)
        }
        lightCycle?.meshes?.get(2)?.material?.emitColor = Vector3f(1.0f, 0.0f, 0.0f)
        lightCycle?.scaleLocal(Vector3f(0.8f))


        enemyCycle = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f), Math.toRadians(90.0f), 0.0f)
        if(enemyCycle == null)
        {
            exitProcess(1)
        }
        enemyCycle?.meshes?.get(2)?.material?.emitColor = Vector3f(1.0f, 0.0f, 0.0f)
        enemyCycle?.scaleLocal(Vector3f(0.5f))
        enemyCycle?.translateLocal(Vector3f(0.0f, 0.0f, -15.0f))


        val diffTex = Texture2D("assets/textures/ground_diff.png", true)
        diffTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val emitTex = Texture2D("assets/textures/ground_emit.png", true)
        emitTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val specTex = Texture2D("assets/textures/ground_spec.png", true)
        specTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundMaterial = Material(diffTex,
            emitTex,
            specTex,
            50.0f,
            Vector2f(64.0f, 64.0f)); GLError.checkThrow()


        //load an object and create a mesh
        val resGround : OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
        //Get the first mesh of the first object
        val groundMesh: OBJMesh = resGround.objects[0].meshes[0]
        val meshGround = Mesh(groundMesh.vertexData, groundMesh.indexData, vertexAttributes, groundMaterial)


        // boden = grün
        ground = Renderable(mutableListOf(meshGround))
        ground.meshes[0].material.emitColor = Vector3f(0f, 1f, 0f)

        // cam
        cam = TronCamera()
        cam.rotateLocal(-35.0f, 0.0f, 0.0f)
        cam.translateLocal(Vector3f(0.0f,  0.0f, 4.0f))
        cam.parent = lightCycle!!

        // Licht
        spotLight = SpotLight(Vector3f(0.0f, 0.5f, -0.7f), Vector3i(255, 255, 255), 16.5f, 20.5f)
        spotLight.parent = lightCycle

        pointLight = PointLight(Vector3f(0.0f, 1.0f, 0.0f), Vector3i(255, 0, 255))
        pointLight.parent = lightCycle

        licht1 = PointLight(Vector3f(15.0f, 2.5f, 0.0f), Vector3i(0, 0, 255))
        licht2 = PointLight(Vector3f(-15.0f, 2.5f, 0.0f), Vector3i(255, 0, 0))
        licht3 = PointLight(Vector3f(0.0f, 2.5f, -15.0f), Vector3i(0, 255, 0))

    }



    fun render(dt: Float, t: Float) {
        // Nichts darf vor dem clear sein
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        cam.bind(staticShader)
        lightCycle?.render(staticShader)
        enemyCycle?.render(staticShader)

        licht1.bind(staticShader, "point1");
        licht2.bind(staticShader, "point2");
        licht3.bind(staticShader, "point3");
        spotLight.bind(staticShader, "spotLight", Matrix4f())
        pointLight.bind(staticShader, "pointLight")

        ground.render(staticShader)
    }

    fun playerWalking(x : Renderable?, dt : Float){
        var speed = 0.0f;
        var rotationDirection = 0.0f
        var turningCycleRadius = 3.0f

        if(window.getKeyState(GLFW.GLFW_KEY_W)) {
            speed = -5.0f
            if (window.getKeyState(GLFW.GLFW_KEY_SPACE)) {
                speed = -15.0f
            }
        }
        else if(window.getKeyState(GLFW.GLFW_KEY_S)) {
            speed = 5.0f
        }

        if(window.getKeyState(GLFW.GLFW_KEY_A)) {
            //rotationDirection = -1.0f
            x?.rotateAroundPoint(0.0f, 1.0f, 0.0f, x!!.getWorldPosition().add(x!!.getXAxis().mul(turningCycleRadius*rotationDirection)))
        }
        else if(window.getKeyState(GLFW.GLFW_KEY_D)) {
            //rotationDirection = 1.0f
            x?.rotateAroundPoint(0.0f, -1.0f, 0.0f, x!!.getWorldPosition().add(x!!.getXAxis().mul(turningCycleRadius*rotationDirection)))
        }
        x?.translateLocal(Vector3f(0.0f, 0.0f, speed * dt))

        /*
          if(rotationDirection != 0.0f){
              x?.translateLocal(Vector3f(0.0f, 0.0f, speed * dt))
          }

        else if(speed != 0.0f)
        {
            x?.rotateAroundPoint(0.0f,  (360 * speed)/(2.0f*Math.PI.toFloat() * turningCycleRadius) * rotationDirection * dt, 0.0f, x!!.getWorldPosition().add(x!!.getXAxis().mul(turningCycleRadius*rotationDirection)))
        }
         */
    }

    fun enemyLogic( enemy : Renderable? , dt : Float , player : Renderable? ){


        var lightX = player?.getWorldPosition();
        var lightY = player?.getWorldPosition();

        var lx : Float = lightX!!.x;
        var ly : Float= lightY!!.z;

        var enemyX = enemy?.getWorldPosition();
        var enemyY = enemy?.getWorldPosition();

        var ex = enemyX!!.x;
        var ey = enemyY!!.z;

        var xDistance = lx - ex;
        var yDistance = ly - ey;


        if (xDistance > 0.1f && xDistance != 0f){
            enemy?.translateLocal(Vector3f(2f * dt, 0f, 0f * dt))
        } else if (xDistance < 1f && xDistance != 0f){
            enemy?.translateLocal(Vector3f(-2f * dt, 0f, 0f * dt))
        }

        if (yDistance > 0.1f && yDistance != 0f) {
            enemy?.translateLocal(Vector3f(0f * dt, 0f, 2f * dt))
            //enemyCycle?.rotateAroundPoint(0.0f,  (360 * speed)/(2.0f*Math.PI.toFloat() * turningCycleRadius) * rotationDirection * dt, 0.0f, enemyCycle!!.getWorldPosition().add(enemyCycle!!.getXAxis().mul(turningCycleRadius*rotationDirection)))

        } else if (yDistance < 1f && yDistance != 0f){
            enemy?.translateLocal(Vector3f(0f * dt, 0f, -2f * dt))
        }

    }

    fun colision(player : Renderable?, enemy : Renderable?):Boolean{

        val playerBox = boxing(player);
        val enemyBox = boxing(enemy);

        // 0 = Object mitte
        // 1 = Oben Rechts
        // 2 = Oben Links
        // 3 = unten Rechts
        // 4 = unten Links

        var playerA : Float = playerBox[3].first - playerBox[4].first
        var playerB : Float = playerBox[2].second - playerBox[4].second

        var enemyA : Float = enemyBox[3].first - enemyBox[4].first
        var enemyB : Float = enemyBox[2].second - enemyBox[4].second

        var b : Boolean = enemyBox[3].first in playerBox[4].first .. playerBox[3].first
        var a : Boolean = enemyBox[2].second in playerBox[4].second .. playerBox[2].second

        var c : Boolean = enemyBox[1].second in playerBox[4].second .. playerBox[2].second
        var d : Boolean = enemyBox[4].second in playerBox[4].second .. playerBox[3].second


        var b_Strich : Float = playerB - enemyB
        var a_Strich : Float = playerA - enemyA

/*
        if (playerBox[1].second > enemyBox[4].second && playerBox[1].first < enemyBox[4].first){
            println("ALARM");
            return true
        }
 */

        // Distanzen der Punkte
        //var xDistance = playerBox[0].first - enemyBox[0].first;
        //var yDistance = playerBox[0].second - enemyBox[0].second;

        //return !(xDistance >= 0.2f && xDistance <= -0.2f || yDistance >= 0.2f && yDistance <= -0.2f)
        return false
    }

    fun boxing(obj : Renderable?): MutableList<Pair<Float,Float>>{
        val playerX = obj?.getWorldPosition();
        val playerZ = obj?.getWorldPosition();

        val lx : Float = playerX!!.x;
        val lz : Float= playerZ!!.z;

        val p0 : Pair<Float,Float> = Pair(lx,lz)
        val p1 : Pair<Float,Float> = Pair(lx + 1.0f, lz + 1.0f) // oben rechts == Z-MAX
        val p3 : Pair<Float,Float> = Pair(lx + 1.0f,lz - 1.0f) // unten rechts == X-Max & Z-MIN
        val p2 : Pair<Float,Float> = Pair(lx - 1.0f,lz + 1.0f) // oben Links
        val p4 : Pair<Float,Float> = Pair(lx - 1.0f,lz - 1.0f) // unten links == X-MIN



       // println(p1); // <-- mittelpunkt der objekte

        return  mutableListOf<Pair<Float,Float>>(p0,p1,p2,p3,p4)
    }


    fun update(dt: Float, t: Float) {

        //enemyLogic(enemyCycle, dt , lightCycle);

        playerWalking(lightCycle, dt);

        if(colision(lightCycle,enemyCycle)){
            //println("Getroffen")
        }

        lightCycle?.meshes?.get(2)?.material?.emitColor = Vector3f((Math.sin(t) + 1.0f)/2, (Math.sin(t*2) + 1.0f)/2, (Math.sin(t*3) + 1.0f)/2)
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    var oldMousePosX = 0.0;
    var oldMousePosY = 0.0;

    fun onMouseMove(xpos: Double, ypos: Double) {

        cam.rotateAroundPoint(0.0f , (oldMousePosX - xpos).toFloat() * 0.1f, 0.0f, Vector3f(0.0f))
        oldMousePosX = xpos
        oldMousePosY = ypos
    }


    fun cleanup() {}
}
