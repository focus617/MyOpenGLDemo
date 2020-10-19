#version 300 es
layout (location = 0) in vec3 a_Position;    //顶点位置
layout (location = 1) in vec4 a_Color;       //顶点颜色

//变换矩阵
uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

out  vec4 v_Color;           //用于传递给片元着色器的变量

void main()
{
    //根据总变换矩阵计算此次绘制此顶点位置
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0);

    //将接收的颜色传递给片元着色器
    v_Color = a_Color;
}
