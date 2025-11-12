import vision.gears.webglmath.*

open class GameObject(vararg meshes : Mesh) 
   : UniformProvider("gameObject") {

  val position = Vec3()
  var roll = 0.0f
  var pitch = 0.0f
  var yaw = 0.0f
  val scale = Vec3(1.0f, 1.0f, 1.0f) 

  // rotation matrix for ball rolling (replaces yaw-pitch-roll for rolling objects)
  val orientationMatrix = Mat4()
  var useOrientationMatrix = false  // Set to true for rolling balls
  
  // physics properties for rolling
  open var velocity = Vec3()
  open var radius = 0.5f  // should match sphere radius
  val groundNormal = Vec3(0f, 1f, 0f)  // upward direction

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
    // for rolling objects: use orientation matrix instead of yaw-pitch-roll
    // first apply rotation, then scale, then translate
    if (useOrientationMatrix) {
      modelMatrix.set(orientationMatrix).
        scale(scale).
        translate(position)
    } else {
      // original behavior for normal non-rolling objects
      modelMatrix.set().
      scale(scale).
      rotate(roll).
      rotate(pitch, 1.0f, 0.0f, 0.0f).
      rotate(yaw, 0.0f, 1.0f, 0.0f).
      translate(position)
    }
  
    modelMatrixInv.set(modelMatrix)
    modelMatrixInv.invert()
  }

  open fun move(dt : Float,
                t : Float = 0.0f,
                keysPressed : Set<String> = emptySet<String>(),
                gameObjects : List<GameObject> = emptyList<GameObject>()
  ): Boolean {
    // apply velocity to position
    position.x += velocity.x * dt
    position.y += velocity.y * dt
    position.z += velocity.z * dt

    
    // implement rolling if gameobject uses orientation matrix
    if (useOrientationMatrix && velocity.length() > 0.001f) {
      val angularSpeed = velocity.length() / radius // compute angular speed from linear velocity: ω = v/r
      
      // Compute rotation axis: perpendicular to both velocity and ground normal
      // axis = groundNormal × velocity (cross product)
      val angularAxis = Vec3(groundNormal).cross(velocity).normalize()
      
      // Angular displacement this frame: θ = ω * dt
      val angleThisFrame = angularSpeed * dt
      
      // Create rotation matrix for this frame's rotation
      val frameRotation = Mat4().rotate(angleThisFrame, angularAxis)
      
      // Append rotation: new_orientation = frameRotation * old_orientation
      // Use premul to multiply from the left
      orientationMatrix.premul(frameRotation)
    }

    return true;
  }
}
