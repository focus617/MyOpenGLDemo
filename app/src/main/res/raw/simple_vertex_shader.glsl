
#version 300 es
layout (location = 0) in vec4 a_Pos;
layout (location = 1) in vec3 a_Color;
uniform mat4 uMVPMatrix;

out vec4 v_Color;

void main()                    
{                            
    v_Color = vec4(a_Color, 1.0);

    gl_Position = uMVPMatrix * a_Pos;
    gl_PointSize = 10.0;          
}
