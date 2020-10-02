
#version 300 es
layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec2 a_TextureCoordinates;
uniform mat4 u_MVPMatrix;

out vec2 v_TextureCoordinates;

void main()                    
{
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_MVPMatrix * vec4(a_Position, 0.0f, 1.0f);
}
