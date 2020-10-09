#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

//uniform samplerCube u_TextureUnit;
in vec3 v_Position;
in vec4 v_Color;

uniform vec3 objectColor;
uniform vec3 lightColor;

out vec4 gl_FragColor;

void main()
{
    gl_FragColor = vec4(lightColor * objectColor, 1.0);
}
