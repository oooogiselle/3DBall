#version 300 es
precision mediump float;

in vec3 modelPosition;   // matches VS
in vec3 normalW;         // matches VS
out vec4 fragmentColor;

uniform struct {
  vec3 lightWoodColor;
  vec3 darkWoodColor;
  float stripeFreq;
  vec3 ambient;
  vec3 lightDir;
} material;

void main(void) {
  float w = fract(modelPosition.x * material.stripeFreq);
  vec3 base = mix(material.lightWoodColor, material.darkWoodColor, w);

  vec3 N = normalize(normalW);
  float ndl = max(dot(N, -normalize(material.lightDir)), 0.0);

  fragmentColor = vec4(base * (material.ambient + ndl), 1.0);
}
