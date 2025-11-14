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
  vec3 position;           
} camera;

uniform struct {
  float time;
} scene;

out vec4 modelPosition;    
out vec4 worldNormal;          
out vec4 worldPosition;
out vec4 texCoord;


void main(void) {
  modelPosition = vertexPosition;
  worldPosition = vertexPosition * gameObject.modelMatrix;
  gl_Position = worldPosition * camera.viewProjMatrix;
  texCoord = vertexTexCoord;
  //worldNormal = gameObject.modelMatrixInverse * vec4(vertexNormal.xyz, 0);
  worldNormal = vec4(vertexNormal.xyz, 0.0) * gameObject.modelMatrix;

}
