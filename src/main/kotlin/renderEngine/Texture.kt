package renderEngine
import org.lwjgl.opengl.GL11.*
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.imageio.ImageIO


class Texture(path: String) {
 private var width = 0
 private var height = 0
 val textureID: Int

 init {
  textureID = load(path)
 }

 private fun load(path: String): Int {
  var pixels: IntArray? = null
  try {
   val image = ImageIO.read(FileInputStream(path))
   width = image.width
   height = image.height
   pixels = IntArray(width * height)
   image.getRGB(0, 0, width, height, pixels, 0, width)
  } catch (e: IOException) {
   e.printStackTrace()
  }
  val data = IntArray(width * height)
  for (i in 0 until width * height) {
   val a = pixels!![i] and -0x1000000 shr 24
   val r = pixels[i] and 0xff0000 shr 16
   val g = pixels[i] and 0xff00 shr 8
   val b = pixels[i] and 0xff
   data[i] = a shl 24 or (b shl 16) or (g shl 8) or r
  }
  val result = glGenTextures()
  glBindTexture(GL_TEXTURE_2D, result)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
  val buffer = ByteBuffer.allocateDirect(data.size shl 2)
   .order(ByteOrder.nativeOrder()).asIntBuffer()
  buffer.put(data).flip()
  glTexImage2D(
   GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
   GL_UNSIGNED_BYTE, buffer
  )
  glBindTexture(GL_TEXTURE_2D, 0)
  return result
 }

 fun bind() {
  glBindTexture(GL_TEXTURE_2D, textureID)
 }

 fun unbind() {
  glBindTexture(GL_TEXTURE_2D, 0)
 }
}