import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL //# GL# we need this for the constants declared ˙HUN˙ a constansok miatt kell
import kotlin.js.Date
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec1
import vision.gears.webglmath.Vec2
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4
import vision.gears.webglmath.Mat4
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos

class Scene (
  val gl : WebGL2RenderingContext)  : UniformProvider("scene") {

  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
  val vsQuad = Shader(gl, GL.VERTEX_SHADER, "quad-vs.glsl")
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
  val fsBackground = Shader(gl, GL.FRAGMENT_SHADER, "background-fs.glsl")
  val fsEnvmapped = Shader(gl, GL.FRAGMENT_SHADER, "envmapped-fs.glsl")
  val vsWood = Shader(gl, GL.VERTEX_SHADER, "wood-vs.glsl")
  val fsWood = Shader(gl, GL.FRAGMENT_SHADER, "wood-fs.glsl")
  
  val woodProgram = Program(gl, vsWood, fsWood)
  val texturedProgram = Program(gl, vsTextured, fsTextured)
  val backgroundProgram = Program(gl, vsQuad, fsBackground)
  val envmappedProgram = Program(gl, vsWood, fsEnvmapped)

  val texturedQuadGeometry = TexturedQuadGeometry(gl)
  val sphereGeometry = SphereGeometry(gl, stacks = 32, slices = 64, radius = 0.5f)

  val woodMaterial = Material(woodProgram).apply {
    this["lightWoodColor"]?.set(Vec3(0.78f, 0.59f, 0.36f))
    this["darkWoodColor"] ?.set(Vec3(0.46f, 0.29f, 0.17f))

    this["stripeFreq"]?.set(6.0f)
    this["ambient"]   ?.set(Vec3(0.15f,0.15f,0.15f))
    this["lightDir"]  ?.set(Vec3(0.0f, -1.0f, -1.0f).normalize())
    this["noiseFreq"]?.set(10.0f)   // adjust for more/less detail
    this["noiseAmp"]?.set(0.3f)     // adjust for stronger/weaker perturbation
    this["noiseExp"]?.set(2.0f)     // adjust for sharper/softer noise
  }
  
  val envTexture = TextureCube(gl,
    "media/posx512.jpg", "media/negx512.jpg",
    "media/posy512.jpg", "media/negy512.jpg",
    "media/posz512.jpg", "media/negz512.jpg"
  )  
  
  val envMaterial = Material(envmappedProgram).apply {
      this["envTexture"]?.set(envTexture)
      this["reflectivity"]?.set(0.35f)
  }


  val gameObjects = ArrayList<GameObject>()

  


  val jsonLoader = JsonLoader()
  /* 
  val slowpokeMeshes = jsonLoader.loadMeshes(gl,
    "media/slowpoke/slowpoke.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/slowpoke/YadonDh.png"))
    },
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/slowpoke/YadonEyeDh.png"))
    }
  )

  val envmapeddSlowpokeMeshes = jsonLoader.loadMeshes(gl,
    "media/slowpoke/slowpoke.json",
    Material(envmappedProgram).apply{
      this["envTexture"]?.set(envTexture)
    },
    Material(envmappedProgram).apply{
      this["envTexture"]?.set(envTexture)
    }
  )
  */

  val backgroundMaterial = Material(backgroundProgram)

  val woodSphereMesh    = Mesh(woodMaterial, sphereGeometry)
  val envSphereMesh     = Mesh(envMaterial,  sphereGeometry)
  val woodBallGO        = GameObject(woodSphereMesh).apply {
    position.set(0f, 0.5f, 0f) 
    useOrientationMatrix = true
    radius = 0.5f 
    orientationMatrix.set() 
  }
  val envWoodBallGO     = GameObject(envSphereMesh).apply { position.set(1.2f, 0.5f, 0f) } // optional second object
  val backgroundMesh    = Mesh(backgroundMaterial, texturedQuadGeometry)
  val backgroundGO      = GameObject(backgroundMesh)
  

  init{
    backgroundMaterial["envTexture"]?.set( this.envTexture )

    //gameObjects += GameObject(*slowpokeMeshes)
    //gameObjects += GameObject(backgroundMesh)
    gameObjects += woodBallGO
    //woodBallGO.velocity.set(1.0f, 0.0f, 0.0f)  // Roll along X axis
    gameObjects += backgroundGO
  }

  val lights = Array<Light>(8) { Light(it) }
  init{
    lights[0].position.set(1.0f, 1.0f, 1.0f, 0.0f).normalize();
    lights[0].powerDensity.set(1.0f, 1.0f, 1.0f);
    lights[1].position.set(10.0f, 10.0f, 10.0f, 1.0f).normalize();
    lights[1].powerDensity.set(1.0f, 0.0f, 1.0f);
  }


  // LABTODO: replace with 3D camera
  val camera = PerspectiveCamera().apply{
    position.set(0f, 1.0f, 5.0f)
    update()
  }

  fun resize(canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
    camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
  }

  val timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame

  init{
    //LABTODO: enable depth test
    gl.enable(GL.DEPTH_TEST)
  }

  @Suppress("UNUSED_PARAMETER")
  fun update(keysPressed : Set<String>) {
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
    timeAtLastFrame = timeAtThisFrame

    //LABTODO: move camera
    camera.move(dt, keysPressed)
    lights[1].position.set(sin(t), cos(t), cos(2f*t), 0f).normalize()
    
    gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
    gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

    gl.enable(GL.BLEND)
    gl.blendFunc(
      GL.SRC_ALPHA,
      GL.ONE_MINUS_SRC_ALPHA)
    
    backgroundGO.position.set(camera.position)

    gameObjects.forEach{ it.move(dt, t, keysPressed, gameObjects) }

    gameObjects.forEach{ it.update() }
    gameObjects.forEach{ it.draw(this, camera, *lights) }
  }
}
