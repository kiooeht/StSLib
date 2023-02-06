package com.evacipated.cardcrawl.mod.stslib.util;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Grayscale {
    private static final String VERT = "uniform mat4 u_projTrans;\n" +
            "\n" +
            "attribute vec4 a_position;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "attribute vec4 a_color;\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoord;\n" +
            "\n" +
            "uniform vec2 u_viewportInverse;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "    v_texCoord = a_texCoord0;\n" +
            "    v_color = a_color;\n" +
            "}";


    private static final String FRAG = "uniform sampler2D u_texture;\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 texColor = texture2D(u_texture, v_texCoord);\n" +
            "    \n" +
            "    float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));\n" +
            "    \n" +
            "	 texColor.rgb = mix(texColor.rgb, vec3(gray), 1.0);\n" +
            "    gl_FragColor = texColor * v_color;\n" +
            "}";

    public static ShaderProgram program = new ShaderProgram(VERT, FRAG);

}
