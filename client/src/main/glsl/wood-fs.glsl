#version 300 es
precision highp float;

in vec4 worldNormal;         // matches VS
in vec4 worldPosition;
in vec4 modelPosition;
in vec4 texCoord;

out vec4 fragmentColor;


uniform struct {
  vec3 lightWoodColor;
  vec3 darkWoodColor;
  float stripeFreq;
  vec3 ambient;
  vec3 lightDir;
  float noiseFreq;    // frequency of noise
  float noiseAmp;     // amplitude of noise perturbation
  float noiseExp;     // exponent shaping 

} material;

float noise(vec3 r) {
  uvec3 s = uvec3(
    0x1D4E1D4E,
    0x58F958F9,
    0x129F129F);
  float f = 0.0;
  for(int i=0; i<16; i++) {
    vec3 sf =
    vec3(s & uvec3(0xFFFF))
  / 65536.0 - vec3(0.5, 0.5, 0.5);
    
    f += sin(dot(sf, r));
    s = s >> 1;
  }
  return f / 32.0 + 0.5;
}

vec3 noiseGrad(vec3 r) {
  uvec3 s = uvec3(
    0x1D4E1D4E,
    0x58F958F9,
    0x129F129F);
  vec3 f = vec3(0, 0, 0);
  for(int i=0; i<16; i++) {
    vec3 sf =
    vec3(s & uvec3(0xFFFF))
  / 65536.0 - vec3(0.5, 0.5, 0.5);
    
    f += cos(dot(sf, r)) * sf;
    s = s >> 1;
  }
  return f;
}

void main(void) {
  //float w = fract(modelPosition.x * material.stripeFreq);

  vec3 p = modelPosition.xyz;

  // Flowing wood: domain warping using noise gradient
  vec3 grad = noiseGrad(p * material.noiseFreq);
  grad = normalize(grad);   
  p += grad * material.noiseAmp;

  // Base ring pattern (warped)
  float w = (p.x + p.z) * material.stripeFreq;

  // Add turbulence
  w += pow(noise(p * material.noiseFreq), material.noiseExp);

  // Wood rings repeat
  w = fract(w);
  vec3 base = mix(material.lightWoodColor, material.darkWoodColor, w);
  vec3 normal = normalize(worldNormal.xyz);
  float ndl = max(dot(normal, -normalize(material.lightDir)), 0.0);

  fragmentColor = vec4(base * (material.ambient + ndl), 1.0);


}
