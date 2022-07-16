attribute vec4 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projModelView;
varying vec2 v_tex0;
void main() {
   gl_Position = u_projModelView * a_position;
   v_tex0 = a_texCoord0;
   gl_PointSize = 1.0;
}