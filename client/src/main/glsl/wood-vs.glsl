#version 300 es
precision highp float;

in vec4 vertexPosition;  
in vec4 vertexNormal;  
in vec4 vertexTexCoord;


uniform struct {
  mat4 modelMatrix;
  mat4 modelMatrixInverse; // inverse(modelMatrix)
} gameObject;

uniform struct {
  mat4 viewProjMatrix;
  vec3 position;           // not used here
} camera;

uniform struct {
  float time;
} scene;

out vec4 modelPosition;    // EXACT name/type used by wood-fs.glsl
out vec4 worldNormal;          // EXACT name/type used by wood-fs.glsl
out vec4 worldPosition;
out vec4 texCoord;


void main(void) {
  modelPosition = vertexPosition;
  worldPosition = vertexPosition * gameObject.modelMatrix;
  gl_Position = worldPosition * camera.viewProjMatrix;
  texCoord = vertexTexCoord;
  worldNormal = gameObject.modelMatrixInverse * vec4(vertexNormal.xyz, 0);
}
