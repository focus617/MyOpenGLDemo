
#version 300 es
layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Color;
layout (location = 2) in vec3 a_DirectionVector;
layout (location = 3) in float a_ParticleStartTime;

uniform mat4 u_MVPMatrix;
uniform float u_Time;

out vec3 v_Color;
out float v_ElapsedTime;

void main()
{
    v_Color = a_Color;
    v_ElapsedTime = u_Time - a_ParticleStartTime;

    vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);

    gl_Position = u_MVPMatrix * vec4(currentPosition, 1.0);
    gl_PointSize = 25.0;

}
