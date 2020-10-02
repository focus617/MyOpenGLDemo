
#version 300 es
layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec3 a_Color;
uniform mat4 uMVPMatrix;

out vec4 v_Color;

void main()                    
{                            
    v_Color = vec4(a_Color, 1.0);

    gl_Position = uMVPMatrix * vec4(a_Position, 0.0f, 1.0f);
    gl_PointSize = 10.0;          
}
