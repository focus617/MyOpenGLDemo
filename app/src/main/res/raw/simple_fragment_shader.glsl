#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform vec4 u_Color;
out vec4 FragColor;

void main()                    		
{                              	
    FragColor = u_Color;
}
