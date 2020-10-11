#version 300 es
layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 2) in vec2 a_TexCoords;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

uniform vec3 u_ViewPos;

out vec3 v_worldSpacePos;
out vec3 v_worldSpaceViewPos;
out vec3 v_Normal;
out vec2 v_TexCoords;

void main()
{
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0);

    // 将着色点和摄像机转换到世界坐标空间
    v_worldSpacePos = vec3(u_ModelMatrix * vec4(a_Position, 1.0));

    //v_worldSpaceViewPos = vec3(u_ViewMatrix * vec4(u_ViewPos, 1.0));
    v_worldSpaceViewPos = u_ViewPos;

    v_Normal = a_Normal;
    v_TexCoords = a_TexCoords;
}


