#version 300 es
precision mediump float;

in vec4 worldNormal;         // matches VS
in vec4 worldPosition;
in vec4 texCoord;

out vec4 fragmentColor;


uniform struct {
  vec3 lightWoodColor;
  vec3 darkWoodColor;
  float stripeFreq;
  vec3 ambient;
  vec3 lightDir;
} material;

void main(void) {
  float w = fract(worldPosition.x * material.stripeFreq);
  vec3 base = mix(material.lightWoodColor, material.darkWoodColor, w);

  vec3 normal = normalize(worldNormal.xyz);
  float ndl = max(dot(normal, -normalize(material.lightDir)), 0.0);

  fragmentColor = vec4(base * (material.ambient + ndl), 1.0);
}
