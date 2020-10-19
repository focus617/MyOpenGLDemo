#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

in  vec4 v_Color;           //接收从顶点着色器过来的参数
out vec4 gl_FragColor;      //输出到的片元颜色

void main()
{
    gl_FragColor = v_Color; //给此片元颜色值
}
