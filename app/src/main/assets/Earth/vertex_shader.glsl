#version 300 es
layout (location = 0) in vec3 a_Position;           //顶点位置
layout (location = 1) in vec3 a_Normal;             //顶点法线
layout (location = 2) in vec2 a_TextureCoordinates;

//变换矩阵
uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

uniform vec3 u_ViewPos;

out  vec3 v_Position;//用于传递给片元着色器的变量

out vec3 v_worldSpacePos;
out vec3 v_worldSpaceViewPos;
out vec3 v_Normal;
out vec2 v_TexCoords;

void main()
{
    //根据总变换矩阵计算此次绘制此顶点位置
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0);

    //将原始顶点位置传递给片元着色器
    v_Position = a_Position;

    // 将着色点和摄像机转换到世界坐标空间
    v_worldSpacePos = vec3(u_ModelMatrix * vec4(a_Position, 1.0));

    v_worldSpaceViewPos = u_ViewPos;

    v_Normal = a_Normal;

    //将顶点的纹理坐标传给片元着色器
    v_TexCoords = a_TextureCoordinates;
}

