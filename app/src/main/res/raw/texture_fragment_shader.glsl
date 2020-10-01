#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform sampler2D u_TextureUnit;
in vec2 v_TextureCoordinates;

out vec4 FragColor;

void main()                    		
{
    FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}
