package cga.exercise.components.shader

//import jdk.internal.loader.Resource
import kotlin.properties.Delegates
import org.lwjgl.opengl.GL32
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader



/*class SkyboxShaderProgram(vertexShaderPath: String, fragmentShaderPath: String, val int: Any) : ShaderProgram(vertexShaderPath, fragmentShaderPath) {


}
 /*enum class ShaderCategory{
    FRAGMENT,
    VERTEX
 }

class SkyboxShaderProgram(file: String, type: ShaderCategory) {
    var id: Int by Delegates.notNull()

    private fun getSource(stream: InputStream, onSuccess: (source: String) -> Unit) {
        var source = String()
        BufferedReader(InputStreamReader(stream)).also { file ->
            while (true) {
                file.readLine()?.also { line ->
                    source += "$line\n"
                } ?: break
            }
            file.close()
        }
        stream.close()
        onSuccess(source)
    }

    init {
        id = when (type) {
            //  ShaderType.GEOMETRY -> GL32.glCreateShader(GL32.GL_GEOMETRY_SHADER)
            ShaderCategory.VERTEX -> GL32.glCreateShader(GL32.GL_VERTEX_SHADER)
            ShaderCategory.FRAGMENT -> GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER)
        }
       /* Resource.getResource(file) { stream ->
            getSource(stream) { source ->
                compile(id, source)
            }
        }
    }
}*/