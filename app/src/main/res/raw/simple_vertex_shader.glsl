
#version 300 es
layout (location = 0) in vec3 aPos;
uniform mat4 uMVPMatrix;
in vec4 a_Color;

out vec4 v_Color;

void main()                    
{                            
    v_Color = a_Color;

    gl_Position = uMVPMatrix * vec4(aPos.x, aPos.y, aPos.z, 1.0);
    gl_PointSize = 10.0;          
}
