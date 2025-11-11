import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.WebGLRenderingContext as GL
import vision.gears.webglmath.Geometry
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SphereGeometry(
  val gl : WebGL2RenderingContext,
  val stacks : Int = 24,
  val slices : Int = 48,
  val radius : Float = 0.5f
) : Geometry() {

  private val vertexBuffer = gl.createBuffer()
  private val vertexNormalBuffer = gl.createBuffer()
  private val vertexTexCoordBuffer = gl.createBuffer()
  private val indexBuffer = gl.createBuffer()

  private val inputLayout = gl.createVertexArray()

  // number of indices to draw
  private var indexCount = 0

  init {
    // --- build arrays ---
    val posList = ArrayList<Float>()
    val nrmList = ArrayList<Float>()
    val uvList  = ArrayList<Float>()
    val idxList = ArrayList<Short>()

    fun push3(dst: MutableList<Float>, x: Float, y: Float, z: Float) {
      dst += x; dst += y; dst += z
    }
    fun push2(dst: MutableList<Float>, u: Float, v: Float) {
      dst += u; dst += v
    }
    fun vertIndex(i: Int, j: Int) = (i * (slices + 1) + j).toShort()

    // vertices (UV sphere)
    for (i in 0..stacks) {
      val v = i.toFloat() / stacks
      val phi = v * PI.toFloat()                // 0..π
      val y = cos(phi)                          // up axis (Y)
      val r = sin(phi)                          // ring radius

      for (j in 0..slices) {
        val u = j.toFloat() / slices
        val th = u * (2f * PI.toFloat())
        val x = r * cos(th)
        val z = r * sin(th)

        // position
        push3(posList, radius * x, radius * y, radius * z)
        // normal (unit sphere)
        push3(nrmList, x, y, z)
        // texcoord (lat/long)
        push2(uvList, u, 1f - v)
      }
    }

    // indices
    for (i in 0 until stacks) {
      for (j in 0 until slices) {
        val a = vertIndex(i, j)
        val b = vertIndex(i + 1, j)
        val c = vertIndex(i + 1, j + 1)
        val d = vertIndex(i, j + 1)
        // two triangles: a-b-c, a-c-d
        idxList += a; idxList += b; idxList += c
        idxList += a; idxList += c; idxList += d
      }
    }
    indexCount = idxList.size

    // --- upload to GPU (match your quad style) ---
    gl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
    gl.bufferData(
      GL.ARRAY_BUFFER,
      Float32Array(posList.toTypedArray()),   // Array<Float>
      GL.STATIC_DRAW
    )

    gl.bindBuffer(GL.ARRAY_BUFFER, vertexNormalBuffer)
    gl.bufferData(
      GL.ARRAY_BUFFER,
      Float32Array(nrmList.toTypedArray()),
      GL.STATIC_DRAW
    )

    gl.bindBuffer(GL.ARRAY_BUFFER, vertexTexCoordBuffer)
    gl.bufferData(
      GL.ARRAY_BUFFER,
      Float32Array(uvList.toTypedArray()),
      GL.STATIC_DRAW
    )

    gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indexBuffer)
    gl.bufferData(
      GL.ELEMENT_ARRAY_BUFFER,
      Uint16Array(idxList.toTypedArray()),    // Array<Short>
      GL.STATIC_DRAW
    )

    // --- input layout (VAO) exactly like your quad ---
    gl.bindVertexArray(inputLayout)

    gl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
    gl.enableVertexAttribArray(0)
    gl.vertexAttribPointer(0, 3, GL.FLOAT, false, 0, 0)

    gl.bindBuffer(GL.ARRAY_BUFFER, vertexNormalBuffer)
    gl.enableVertexAttribArray(1)
    gl.vertexAttribPointer(1, 3, GL.FLOAT, false, 0, 0)

    gl.bindBuffer(GL.ARRAY_BUFFER, vertexTexCoordBuffer)
    gl.enableVertexAttribArray(2)
    gl.vertexAttribPointer(2, 2, GL.FLOAT, false, 0, 0)

    gl.bindVertexArray(null)
  }

  override fun draw() {
    gl.bindVertexArray(inputLayout)
    gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indexBuffer)
    gl.drawElements(GL.TRIANGLES, indexCount, GL.UNSIGNED_SHORT, 0)
  }
}
