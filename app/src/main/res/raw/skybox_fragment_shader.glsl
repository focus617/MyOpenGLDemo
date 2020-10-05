#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform samplerCube u_TextureUnit;
in vec3 v_Position;

out vec4 gl_FragColor;
	    	   								
void main()                    		
{
	gl_FragColor = texture(u_TextureUnit, v_Position);
}
