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
  float noiseExp;     // exponent shaping (>=1)

} material;

float snoise(vec3 r) {
  vec3 s = vec3(7502, 22777, 4767);
  float f = 0.0;
  for(int i=0; i<16; i++) {
    f += sin( dot(s - vec3(32768, 32768, 32768), r)
                                 / 65536.0);
    s = mod(s, 32768.0) * 2.0 + floor(s / 32768.0);
  }
  return f / 32.0 + 0.5;
}

void main(void) {
  //float w = fract(modelPosition.x * material.stripeFreq);
  float w = fract( (modelPosition.x + modelPosition.z) * material.stripeFreq
   + pow(
     snoise(modelPosition.xyz * material.noiseFreq),
     material.noiseExp)
     * material.noiseAmp
  );

  vec3 base = mix(material.lightWoodColor, material.darkWoodColor, w);

  vec3 normal = normalize(worldNormal.xyz);
  float ndl = max(dot(normal, -normalize(material.lightDir)), 0.0);

  fragmentColor = vec4(base * (material.ambient + ndl), 1.0);
}
