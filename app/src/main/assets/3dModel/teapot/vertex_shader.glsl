#version 300 es
layout (location = 0) in vec3 a_Position;    //顶点位置

//变换矩阵
uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

out vec3 v_Position;  //用于传递给片元着色器的顶点位置

void main() {                            		
   gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0f);

   v_Position = a_Position; //将顶点位置传递给片元着色器
}