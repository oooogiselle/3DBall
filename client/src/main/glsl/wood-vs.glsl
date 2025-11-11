#version 300 es
precision highp float;

layout(location = 0) in vec3 vertexPosition;  // VAO has 3 floats
layout(location = 1) in vec3 vertexNormal;    // VAO has 3 floats

uniform struct {
  mat4 modelMatrix;
  mat4 modelMatrixInverse; // inverse(modelMatrix)
} gameObject;

uniform struct {
  mat4 viewProjMatrix;
  vec3 position;           // not used here
} camera;

out vec3 modelPosition;    // EXACT name/type used by wood-fs.glsl
out vec3 normalW;          // EXACT name/type used by wood-fs.glsl

void main(void) {
  modelPosition = vertexPosition;

  // world position then to clip (mat * vec)
  vec4 pw = gameObject.modelMatrix * vec4(vertexPosition, 1.0);
  gl_Position = camera.viewProjMatrix * pw;

  // proper normal transform: inverse-transpose(model)
  mat3 nMat = mat3(transpose(gameObject.modelMatrixInverse));
  normalW = normalize(nMat * vertexNormal);
}
