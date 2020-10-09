
#version 300 es
layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

out vec3 v_FragPosition;
out vec3 v_Normal;

void main()
{
    v_Normal = a_Normal;

    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0f);

    v_FragPosition = vec3(u_ModelMatrix * vec4(a_Position, 1.0f));
}

