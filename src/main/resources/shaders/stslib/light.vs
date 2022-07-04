attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoords;

void main()
{
    v_color = a_color;
    v_color.r = v_color.r * 1.15;
    v_color.g = v_color.g * 1.15;
    v_color.b = v_color.b * 1.15;
    v_texCoords = a_texCoord0;
    v_color.a = pow(v_color.a * (255.0/254.0) + 0.5, 1.709);
    gl_Position =  u_projTrans * a_position;
}
