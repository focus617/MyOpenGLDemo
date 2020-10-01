#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

in vec4 v_Color;
out vec4 FragColor;

void main()                    		
{                              	
    FragColor = v_Color;
}
