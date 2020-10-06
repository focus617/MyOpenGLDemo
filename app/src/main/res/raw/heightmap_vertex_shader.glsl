#version 300 es
layout (location = 0) in vec3 a_Position;

uniform mat4 u_MVPMatrix;

out vec3 v_Color;

void main()
{
    v_Color = mix(vec3(0.180f, 0.467f, 0.153f),    // A dark green
                  vec3(0.660f, 0.670f, 0.680f),    // A stony gray
                  a_Position.y);

    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0f);
}
