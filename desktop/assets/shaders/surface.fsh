#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_tex0;
uniform sampler2D u_sampler0;

uniform float uTextureS, uTextureT;
uniform float uTextureWidth, uTextureHeight;

void main() {
  float s = uTextureS + fract(v_tex0.s)*uTextureWidth;
  float t = uTextureT + fract(v_tex0.t)*uTextureHeight;
   gl_FragColor = vec4(1, 1, 1, 1) *  texture2D(u_sampler0,  vec2(s, t));
}