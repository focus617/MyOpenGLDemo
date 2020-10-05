
#version 300 es
layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Color;

uniform mat4 u_MVPMatrix;

out vec3 v_Position;
out vec4 v_Color;

void main()
{
    v_Position = a_Position;

    v_Color = vec4(a_Color, 1.0f);

    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0f);
}
