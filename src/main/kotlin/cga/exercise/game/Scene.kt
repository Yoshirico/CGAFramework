package cga.exercise.game

import MyArenaDefence.Enemy
import MyArenaDefence.Player
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.*
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
//import cga.exercise.components.shader.SkyboxShaderProgram
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
import kotlin.math.pow

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    //private val skyboxShader: SkyboxShaderProgram
    private val enemyOn : Boolean = true

    private val cam : TronCamera

    private var ground : Renderable
    private var arena : Renderable
    private var gate1 : Renderable
    private var gate2 : Renderable
    private var gate3 : Renderable
    private var leben1 : Renderable
    var enemys = arrayListOf<Enemy>()

    private var licht1 : PointLight
    private var licht2 : PointLight
    private var licht3 : PointLight

    private var pointLight : PointLight
    private var spotLight : SpotLight

    var player : Player

    var anzahlGegner = 3
    var p = -15.0f

    private var jumpSpeed = 0f
    private var jumpDirection = false // False = going up, True = going down
    private var canJump = true

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

        //SkyboxShaderProgramm

        //skyboxShader = SkyboxShaderProgram("assets/shaders/skybox_vert.glsl", "assets/shaders/skybox_frag.glsl")

        //Create the mesh
        val stride: Int = 8 * 4
        val attrPos = VertexAttribute(3, GL_FLOAT, stride, 0) //position
        val attrTC = VertexAttribute(2, GL_FLOAT, stride, 3 * 4) //textureCoordinate
        val attrNorm = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normalval
        val vertexAttributes = arrayOf<VertexAttribute>(attrPos, attrTC, attrNorm)

        player = Player("assets/Light Cycle/avatar/Zack.obj",-85f,enemys)

        //gate1
        val diff3Tex = Texture2D("assets/textures/medieval_wood_diff.png", true)
        diff3Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val emit3Tex = Texture2D("assets/textures/medieval_wood_emit.png", true)
        emit3Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val spec3Tex = Texture2D("assets/textures/medieval_wood_spec.png", true)
        spec3Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val gate1Material = Material(diff3Tex,emit3Tex,spec3Tex,50.0f,Vector2f(5.0f, 5.0f)); GLError.checkThrow()

        //load an object and create a mesh
        val resGate1 : OBJResult = OBJLoader.loadOBJ("assets/arena/gate1.obj")
        //Get the first mesh of the first object
        val gate1Mesh: OBJMesh = resGate1.objects[0].meshes[0]
        val meshGate1 = Mesh(gate1Mesh.vertexData, gate1Mesh.indexData, vertexAttributes,gate1Material) //arenaMaterial

        gate1 = Renderable(mutableListOf(meshGate1))

        //gate2
        val diff4Tex = Texture2D("assets/textures/medieval_wood_diff.png", true)
        diff4Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val emit4Tex = Texture2D("assets/textures/medieval_wood_emit.png", true)
        emit4Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val spec4Tex = Texture2D("assets/textures/medieval_wood_spec.png", true)
        spec4Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val gate2Material = Material(diff4Tex,emit4Tex,spec4Tex,50.0f,Vector2f(5.0f, 5.0f)); GLError.checkThrow()

        //load an object and create a mesh
        val resGate2 : OBJResult = OBJLoader.loadOBJ("assets/arena/gate2.obj")
        //Get the first mesh of the first object
        val gate2Mesh: OBJMesh = resGate2.objects[0].meshes[0]
        val meshGate2 = Mesh(gate2Mesh.vertexData, gate2Mesh.indexData, vertexAttributes,gate2Material) //arenaMaterial

        gate2 = Renderable(mutableListOf(meshGate2))

        //gate3
        val diff5Tex = Texture2D("assets/textures/medieval_wood_diff.png", true)
        diff5Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val emit5Tex = Texture2D("assets/textures/medieval_wood_emit.png", true)
        emit5Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val spec5Tex = Texture2D("assets/textures/medieval_wood_spec.png", true)
        spec5Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val gate3Material = Material(diff5Tex,emit5Tex,spec5Tex,50.0f,Vector2f(5.0f, 5.0f)); GLError.checkThrow()

        //load an object and create a mesh
        val resGate3 : OBJResult = OBJLoader.loadOBJ("assets/arena/gate3.obj")
        //Get the first mesh of the first object
        val gate3Mesh: OBJMesh = resGate3.objects[0].meshes[0]
        val meshGate3 = Mesh(gate3Mesh.vertexData, gate3Mesh.indexData, vertexAttributes,gate3Material) //arenaMaterial

        gate3 = Renderable(mutableListOf(meshGate3))

        //leben
        val diff2Tex = Texture2D("assets/textures/herz_diff.png", true)
        diff2Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val emit2Tex = Texture2D("assets/textures/herz_emit.png", true)
        emit2Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val spec2Tex = Texture2D("assets/textures/herz_spec.png", true)
        spec2Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val leben1Material = Material(diff2Tex,emit2Tex,spec2Tex,50.0f,Vector2f(1.0f, 1.0f)); GLError.checkThrow()

        //load an object and create a mesh
        val resLeben1 : OBJResult = OBJLoader.loadOBJ("assets/life/heartObj.obj")
        //Get the first mesh of the first object
        val leben1Mesh: OBJMesh = resLeben1.objects[0].meshes[0]
        val meshLeben1 = Mesh(leben1Mesh.vertexData, leben1Mesh.indexData, vertexAttributes,leben1Material) //arenaMaterial

        leben1 = Renderable(mutableListOf(meshLeben1))
        leben1.scaleLocal(Vector3f(0.1f))
        leben1.translateGlobal(Vector3f(0f , 2f, 0f ))
        leben1.rotateLocal(0f,0f,0f)

        //Arena
        val diff1Tex = Texture2D("assets/textures/arena_diff.png", true)
        diff1Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val emit1Tex = Texture2D("assets/textures/arena_emit.png", true)
        emit1Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val spec1Tex = Texture2D("assets/textures/arena_spec.png", true)
        spec1Tex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val arenaMaterial = Material(diff1Tex,emit1Tex,spec1Tex,50.0f,Vector2f(1.0f, 1.0f)); GLError.checkThrow()

        //load an object and create a mesh
        val resArena : OBJResult = OBJLoader.loadOBJ("assets/arena/arena_v1.obj")
        //Get the first mesh of the first object
        val arenaMesh: OBJMesh = resArena.objects[0].meshes[0]
        val meshArena = Mesh(arenaMesh.vertexData, arenaMesh.indexData, vertexAttributes,arenaMaterial) //arenaMaterial

        arena = Renderable(mutableListOf(meshArena))

        //Boden
        val diffTex = Texture2D("assets/textures/Stone_Floor_diff.png", true)
        diffTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val emitTex = Texture2D("assets/textures/Stone_Floor_emit.png", true)
        emitTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        val specTex = Texture2D("assets/textures/Stone_Floor_spec.png", true)
        specTex.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundMaterial = Material(diffTex,emitTex, specTex,50.0f,Vector2f(10.0f, 10.0f)); GLError.checkThrow()

        //load an object and create a mesh
        val resGround : OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
        //Get the first mesh of the first object
        val groundMesh: OBJMesh = resGround.objects[0].meshes[0]
        val meshGround = Mesh(groundMesh.vertexData, groundMesh.indexData, vertexAttributes,groundMaterial) //groundMaterial

        ground = Renderable(mutableListOf(meshGround))

        // cam
        cam = TronCamera()
        cam.rotateLocal(-35.0f, 0.0f, 0.0f)
        cam.translateLocal(Vector3f(0.0f,  0.0f, 4.0f))
        cam.parent = player.player
        leben1.parent = player.player

        // Licht
        spotLight = SpotLight(Vector3f(5.0f, 1.0f, 1.0f), Vector3i(255, 255, 255), 16.5f, 20.5f)
        pointLight = PointLight(Vector3f(0.0f, 1.5f, 0.0f), Vector3i(0, 0, 255))
        licht1 = PointLight(Vector3f(2.0f, 0.0f, 1.0f), Vector3i(255, 255, 255))
        licht2 = PointLight(Vector3f(3.0f, 0.0f, 1.0f), Vector3i(255, 255, 255))
        licht3 = PointLight(Vector3f(4.0f, 0.0f, 1.0f), Vector3i(255, 255, 255))

        pointLight.parent =player.player
        //fun placeEnemys
        while (anzahlGegner > 0){
            enemys.add(Enemy("assets/enemy/enemy.obj", 0f))

            anzahlGegner -= 1
        }
        for(i in enemys){
            i.enemy?.translateLocal(Vector3f(p, 3.0f, 5f))
            p += 5f
        }

    }

    fun render(dt: Float, t: Float) {
        // Nichts darf vor dem clear sein
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        cam.bind(staticShader)
        leben1.render(staticShader)
        player.player?.render(staticShader)
        licht1.bind(staticShader, "point1");
        licht2.bind(staticShader, "point2");
        licht3.bind(staticShader, "point3");
        spotLight.bind(staticShader, "spotLight", Matrix4f())
        pointLight.bind(staticShader, "pointLight")
        arena.render(staticShader)
        gate1.render(staticShader)
        gate2.render(staticShader)
        gate3.render(staticShader)
        ground.render(staticShader)

        for (i in enemys){
            i.enemy?.render(staticShader)
        }

    }

    fun distanceToSomething(player : Renderable? , enemy : Renderable?): Pair<Float,Float>{

        val playerX = player?.getWorldPosition();
        val playerY = player?.getWorldPosition();

        val px : Float = playerX!!.x
        val pz : Float= playerY!!.z

        val enemyX = enemy?.getWorldPosition();
        val enemyY = enemy?.getWorldPosition();

        val ex = enemyX!!.x
        val ez = enemyY!!.z

        val xDistance = px - ex
        val zDistance = pz - ez

        return Pair(xDistance,zDistance)
    }

    fun followMe(player: Renderable?, enemy: Renderable?): Double {
        val dist = distanceToSomething(player, enemy)
        return Math.toDegrees(Math.atan2(dist.second.toDouble(), dist.first.toDouble()))
    }

    fun colision(player2 : Renderable?, enemy : Renderable?, dt : Float):Boolean{

        val playerBox = boxing(player2);
        val enemyBox = boxing(enemy);
        val distance = distanceToSomething(player2,enemy)

        val px = playerBox[4]
        val pz = playerBox[5]

        if(px < enemyBox[0] && px > enemyBox[2] && pz < enemyBox[1] && pz > enemyBox[3]){
            if (distance.first > distance.second){
                // X
                if(pz > enemyBox[3] && pz < (enemyBox[3] + 1.5f)){
                    player2?.translateLocal(Vector3f(0f, 0f, -20f * dt))
                    println("Hinten")
                    return true
                }
                if (px < enemyBox[0] && px > (enemyBox[0] - 1.5f)){
                    player2?.translateLocal(Vector3f(-20f * dt, 0f, 0f ))
                    println("Rechts")
                    return true
                }
            } else {
                //Z
                if(pz < enemyBox[1] && pz > (enemyBox[1] - 1.5f)){
                    player2?.translateLocal(Vector3f(0f, 0f, 20f * dt ))
                    println("vorne")
                    return true
                }
                if(px > enemyBox[2] && px < (enemyBox[2] + 1.5f)){
                    player2?.translateLocal(Vector3f(0f, 0f, 20f * dt ))
                    println("Links")
                    return true
                }
            }
        }
        return false
    }

    fun boxing(obj : Renderable?): MutableList<Float>{

        val playerx = obj?.getWorldPosition()
        val playerz = obj?.getWorldPosition()

        val lx : Float = playerx!!.x
        val lz : Float= playerz!!.z

        val posX = lx + 1.5f // 0
        val posY = lz + 1.5f // 1
        val negX = lx - 1.5f // 2
        val negY = lz - 1.5f // 3

        // println(p1); // <-- mittelpunkt der objekte

        return  mutableListOf<Float>(posX, posY, negX, negY,lx,lz);
    }

    fun roundCollision(obj: Enemy, dt : Float) {
        var myReturn : Int = 10
        for (i in enemys) {
            if (i.enemy !== obj.enemy) {
                val dist = distanceToSomething(i.enemy, obj.enemy)
                val squaredDis = Math.sqrt(
                    dist.first * dist.first + dist.second * dist.second
                )
                myReturn = squaredDis.toInt()

            }
        }
        obj.drive(dt, 2f)
    }


    fun checkCollisionWithMap(): Boolean{
        if (player.player!!.getWorldPosition().y <= 0){
            return true
        }
        return false
    }

    fun ich_hab_langsam_keine_ahnung_mehr_wie_ich_die_ganzen_funktionen_nennen_soll( ich_brauch_ne_variable : Pair<Float,Float>): Float{
        val berechnungDiesachenBerechnet = Math.sqrt(ich_brauch_ne_variable.first*ich_brauch_ne_variable.first + ich_brauch_ne_variable.second*ich_brauch_ne_variable.second)
        return berechnungDiesachenBerechnet
    }
    var timebefore = 0
    fun attack(obj: Enemy):Boolean {
        var myReturn : Int = 10

        val dist = distanceToSomething(player.player, obj.enemy)
        val squaredDis = Math.sqrt(
            dist.first * dist.first + dist.second * dist.second
        )
        if(squaredDis.toInt() < 6 ){
            return true
        }
        return false

    }
    fun update(dt: Float, t: Float) {

        if (window.getKeyState(GLFW.GLFW_KEY_E)){
            for (you in enemys){
                if (attack(you)){
                    you.health -= 50
                }
            }

        }

        for (enemy in enemys){
            if (enemy.health <= 0){
                enemy.isOn = false
                enemy.enemy?.translateLocal(Vector3f(200f,0f,0f))
            }
        }

        player.playerWalking(player.player, dt, window, cam)

        if (window.getKeyState(GLFW.GLFW_KEY_C)){
            leben1.translateLocal(Vector3f(0f,20f,0f))
        }

        // Player is on the ground
        if (checkCollisionWithMap()) {
            jumpSpeed = 0f
            canJump = true
            jumpDirection = false
        }

        // Player is on the ground and presses space
        if (window.getKeyState(GLFW.GLFW_KEY_SPACE) && canJump) {
            canJump = false
            jumpSpeed = 1.2f // war -0.015
            player.player!!.setPosition(player.player!!.getWorldPosition().x, 0.1f, player.player!!.getWorldPosition().z)
        }

        // Player is airborne
        if (!canJump) {

            if (jumpDirection){
                jumpSpeed += 1f * 0.005f
                println(jumpSpeed)
            } else {
                jumpSpeed -= 1f * 0.005f
                println(jumpSpeed)
            }

            // Calculate jumping vector
            val jumpingVector = player.player!!.getWorldPosition().y * jumpSpeed

            val oldCharacterPosition = player.player!!.getWorldPosition()
            val newCharacterPosition = oldCharacterPosition
            newCharacterPosition.y = jumpingVector

            player.player!!.setPosition(newCharacterPosition.x, newCharacterPosition.y, newCharacterPosition.z)

            if (jumpSpeed < 0.02) {
                jumpDirection = true
                player.player!!.setPosition(player.player!!.getWorldPosition().x, 0.0f, player.player!!.getWorldPosition().z)
            }
        }


        if (enemyOn){
            for (i in enemys) {
                if (i.isOn) {
                    val deg = followMe(player.player, i.enemy).toFloat()
                    val x = distanceToSomething(player.player, i.enemy)

                    i.enemyLogic(
                        player.player,
                        dt,
                        x,
                        deg
                    )

                    i.drive(dt, 5f)

                    if (ich_hab_langsam_keine_ahnung_mehr_wie_ich_die_ganzen_funktionen_nennen_soll(
                            distanceToSomething(
                                player.player,
                                i.enemy
                            )
                        ) < 5f
                    ) {
                        if (colision(player.player, i.enemy, dt)) {
                            player.takeDamage(i.damage)
                        }
                    }
                }
            }
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    var oldMousePosX = 0.0;
    var oldMousePosY = 0.0;

    fun onMouseMove(xpos: Double, ypos: Double) {
        player.player?.rotateAroundPoint(0f , (oldMousePosX - xpos).toFloat() * 0.1f, 0.0f, player.player!!.getWorldPosition())
        oldMousePosX = xpos
        oldMousePosY = ypos
    }

    fun cleanup() {}
}