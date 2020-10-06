#version 300 es
layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;

uniform mat4 u_MVPMatrix;
uniform vec3 u_VectorToLight;

out vec3 v_Color;

void main()
{
    v_Color = mix(vec3(0.180f, 0.467f, 0.153f),    // A dark green
                  vec3(0.660f, 0.670f, 0.680f),    // A stony gray
                  a_Position.y);

    vec3 scaledNormal = a_Normal;
    scaledNormal.y *= 10.0;
    scaledNormal = normalize(scaledNormal);

    float diffuse = max(dot(scaledNormal, u_VectorToLight), 0.0);
    // diffuse *= 0.3;
    v_Color *= diffuse;

    float ambient = 0.2;
    v_Color += ambient;

    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0f);
}
