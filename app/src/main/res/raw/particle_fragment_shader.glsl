#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

in vec3 v_Color;
in float v_ElapsedTime;

out vec4 gl_FragColor;

void main()
{
    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);
}

