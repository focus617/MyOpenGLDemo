#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

in vec3 v_Position;
in vec4 v_Color;

out vec4 gl_FragColor;

void main()
{
    gl_FragColor = vec4(1.0); // 将向量的四个分量全部设置为1.0
}
