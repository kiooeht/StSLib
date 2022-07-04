#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main()
{
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    gl_FragColor.r = gl_FragColor.r * 1.15;
    gl_FragColor.g = gl_FragColor.g * 1.15;
    gl_FragColor.b = gl_FragColor.b * 1.15;
}
