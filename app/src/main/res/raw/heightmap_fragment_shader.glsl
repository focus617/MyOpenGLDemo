#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform sampler2D u_TextureUnit1;
uniform sampler2D u_TextureUnit2;

in vec3 v_Color;
in vec2 v_TextureCoordinates;
in float v_Ratio;

out vec4 gl_FragColor;

void main()                    		
{
    gl_FragColor = texture(u_TextureUnit1, v_TextureCoordinates) * (1.0 - v_Ratio);
    // Divide the texture coordinates by 2 to make the stone texture repeat half as often.
    gl_FragColor += texture(u_TextureUnit2, v_TextureCoordinates / 2.0) * v_Ratio;
    gl_FragColor *= vec4(v_Color, 1.0);
}
