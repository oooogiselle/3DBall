import vision.gears.webglmath.*

open class GameObject(vararg meshes : Mesh) 
   : UniformProvider("gameObject") {

  val position = Vec3()
  var roll = 0.0f
  var pitch = 0.0f
  var yaw = 0.0f
  val scale = Vec3(1.0f, 1.0f, 1.0f) 

  val modelMatrix by Mat4 ()
  val modelMatrixInv by Mat4 ()
  val spriteSacle by Vec2 ()
  val spriteOffset by Vec2 ()

  var counter = 0

  init { 
    addComponentsAndGatherUniforms(*meshes)
    spriteSacle.set(1.0f, 1.0f)
    spriteOffset.set(0.0f, 0.0f)
  }
  fun update() {
    //PRACTICAL TODO: replace this with better solution
    modelMatrix.set().
    scale(scale).
    rotate(roll).
    rotate(pitch, 1.0f, 0.0f, 0.0f).
    rotate(yaw, 0.0f, 1.0f, 0.0f).
    translate(position)

    modelMatrixInv.set(modelMatrix)
    modelMatrixInv.invert()
  }

  open fun move(dt : Float,
                t : Float = 0.0f,
                keysPressed : Set<String> = emptySet<String>(),
                gameObjects : List<GameObject> = emptyList<GameObject>()
  ): Boolean {
    return true;
  }
}
