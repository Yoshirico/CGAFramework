package cga.exercise.game

import MyArenaDefence.Enemy
import MyArenaDefence.Player
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
import kotlin.system.exitProcess


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram

    private val cam : TronCamera

    private var ground : Renderable
    var lightCycle : Renderable?
    var enemys = arrayListOf<Enemy>()

    private var licht1 : PointLight
    private var licht2 : PointLight
    private var licht3 : PointLight

    private var pointLight : PointLight
    private var spotLight : SpotLight

    var player : Player

    var x = 0
    var p = -2.0f




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

        player = Player("assets/Light Cycle/avatar/Zack.obj",-85f)

        lightCycle = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f), Math.toRadians(90.0f), 0.0f)
        if(lightCycle == null)
        {
            exitProcess(1)
        }
        lightCycle?.meshes?.get(2)?.material?.emitColor = Vector3f(1.0f, 0.0f, 0.0f)
        lightCycle?.scaleLocal(Vector3f(0.8f))

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
        cam.parent = player?.player!!

        // Licht
        spotLight = SpotLight(Vector3f(0.0f, 0.5f, -0.7f), Vector3i(255, 255, 255), 16.5f, 20.5f)
        spotLight.parent = lightCycle

        pointLight = PointLight(Vector3f(0.0f, 0.0f, 3.0f), Vector3i(255, 255, 255))
        licht1 = PointLight(Vector3f(1.0f, 0.0f, 1.0f), Vector3i(255, 255, 255))
        licht2 = PointLight(Vector3f(-1.0f, 0.0f, 1.0f), Vector3i(255, 255, 255))
        licht3 = PointLight(Vector3f(1.0f, 2000.5f, -1.0f), Vector3i(255, 255, 255))

        pointLight.parent = lightCycle


        while (x != 11){
            enemys.add(Enemy("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", 0f))
            x += 1
        }
        for(i in enemys){
            i.enemy?.translateLocal(Vector3f(p, 0.0f, 0f))
            p += 3f
        }
    }



    fun render(dt: Float, t: Float) {
        // Nichts darf vor dem clear sein
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        cam.bind(staticShader)

        lightCycle?.render(staticShader)
        player.player?.render(staticShader)

        for (i in enemys){
            i.enemy?.render(staticShader)
        }

        licht1.bind(staticShader, "point1");
        licht2.bind(staticShader, "point2");
        licht3.bind(staticShader, "point3");
        spotLight.bind(staticShader, "spotLight", Matrix4f())
        pointLight.bind(staticShader, "pointLight")

        ground.render(staticShader)
    }

    fun playerWalking(x : Renderable?, dt : Float){
        var speed = 0f
        var rotationDirection = 0f
        var turningCycleRadius = 3f


            if(window.getKeyState(GLFW.GLFW_KEY_W)) {
                speed = -5.0f
                if (window.getKeyState(GLFW.GLFW_KEY_SPACE)) {
                    speed = -15.0f
                }
            }


        if(window.getKeyState(GLFW.GLFW_KEY_S)) {
            speed = 5.0f
        }

        if(window.getKeyState(GLFW.GLFW_KEY_A)) {
            //rotationDirection = -1.0f
            x?.translateLocal(Vector3f(-5f * dt, 0.0f, 0f))
            //x?.rotateAroundPoint(0.0f, 1.0f, 0.0f, x!!.getWorldPosition().add(x!!.getXAxis().mul(turningCycleRadius*rotationDirection)))
        }
        else if(window.getKeyState(GLFW.GLFW_KEY_D)) {
            x?.translateLocal(Vector3f(5f * dt, 0.0f, 0f))
            //rotationDirection = 1.0f
            //x?.rotateAroundPoint(0.0f, -1.0f, 0.0f, x!!.getWorldPosition().add(x!!.getXAxis().mul(turningCycleRadius*rotationDirection)))
        }

        x?.translateLocal(Vector3f(0.0f, 0.0f, speed * dt))

    }

    fun distanceToSomething(player : Renderable? , enemy : Renderable?): Pair<Float,Float>{

        var playerX = player?.getWorldPosition();
        var playerY = player?.getWorldPosition();

        var px : Float = playerX!!.x;
        var pz : Float= playerY!!.z;

        var enemyX = enemy?.getWorldPosition();
        var enemyY = enemy?.getWorldPosition();

        var ex = enemyX!!.x;
        var ez = enemyY!!.z;

        var xDistance = px - ex;
        var zDistance = pz - ez;

        return Pair(xDistance,zDistance)
    }

    fun followMe(player: Renderable?, enemy: Renderable?): Double {
        val dist = distanceToSomething(player, enemy)
        return Math.toDegrees(Math.atan2(dist.second.toDouble(), dist.first.toDouble()))
    }

    fun colision(player : Renderable?, enemy : Renderable?, dt : Float): Int{

        val playerBox = boxing(player);
        val enemyBox = boxing(enemy);
        val distance = distanceToSomething(player,enemy)

        val px = playerBox[4]
        val pz = playerBox[5]

        if(px < enemyBox[0] && px > enemyBox[2] && pz < enemyBox[1] && pz > enemyBox[3]){
            if (distance.first > distance.second){
                // X
                if(pz > enemyBox[3] && pz < (enemyBox[3] + 1.5f)){
                    player?.translateLocal(Vector3f(0f, 0f, 2f * dt))
                    //println("Gerade Aus")
                    return 2
                }
                if (px < enemyBox[0] && px > (enemyBox[0] - 1.5f)){
                    player?.translateLocal(Vector3f(-2f * dt, 0f, 0f ))
                   // println("Rechts")
                    return 2
                }
            } else {
                //Z
                if(pz < enemyBox[1] && pz > (enemyBox[1] - 1.5f)){
                    player?.translateLocal(Vector3f(0f, 0f, -2f * dt ))
                   // println("Hinten")
                    return 2
                }
                if(px > enemyBox[2] && px < (enemyBox[2] + 1.5f)){
                    player?.translateLocal(Vector3f(0f, 0f, 2f * dt ))
                    //println("Links")
                    return 2
                }
            }
        }
    return 0
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

    fun update(dt: Float, t: Float) {

        for(i in enemys){
            if (colision(player.player,i.enemy,dt) == 0) {

            } else {
                player.player?.translateLocal(Vector3f(0f, 0.0f, 5f * dt))
            }
        }

        if (player.health > 0) {
            playerWalking(player.player, dt)
        }

        for (i in enemys){
            val deg = followMe(player.player,i.enemy).toFloat()

            val x = distanceToSomething(player.player,i.enemy)
            //enemyLogic(i.enemy, dt, player.player, x )
            i.enemyLogic(player.player, dt, x)

            if (i.radY < deg){
                i.enemy?.rotateAroundPoint(0f, -2f,0f ,i.enemy!!.getWorldPosition())
                i.radY += 2f
            } else if (i.radY >= deg){
                i.enemy?.rotateAroundPoint(0f, 2f,0f ,i.enemy!!.getWorldPosition())
                i.radY -= 2f
            }

            if (colision(player.player,i.enemy, dt) != 0){
                player.takeDamage(i.damage)
                println(player.health)
            }

        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    var oldMousePosX = 0.0;
    var oldMousePosY = 0.0;

    fun onMouseMove(xpos: Double, ypos: Double) {
        player?.player?.rotateAroundPoint(0.0f , (oldMousePosX - xpos).toFloat() * 0.1f, 0.0f, player?.player!!.getWorldPosition())
        oldMousePosX = xpos
        oldMousePosY = ypos
    }


    fun cleanup() {}
}
