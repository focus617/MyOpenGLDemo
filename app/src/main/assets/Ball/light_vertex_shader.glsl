
#version 300 es
layout (location = 0) in vec3 a_Position;//顶点位置
layout (location = 1) in vec3 a_Normal;//顶点法线

//变换矩阵
uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

void main()
{
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0f);
}
