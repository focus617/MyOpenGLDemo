#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

in vec3 v_FragPosition;
in vec3 v_Normal;

uniform vec3 objectColor;
uniform vec3 lightColor;
uniform vec3 lightPosition;

out vec4 gl_FragColor;

vec3 getAmbientLighting();
vec3 getDirectionalLighting();
vec3 getPointLighting();

void main()
{
    vec3 result = getAmbientLighting();
    result += getDirectionalLighting();

    gl_FragColor = vec4(result, 1.0);
}

vec3 getAmbientLighting()
{
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;
    return ambient * objectColor;
}

vec3 getDirectionalLighting()
{
    vec3 scaledNormal = normalize(v_Normal);
    vec3 lightDir = normalize(lightPosition - v_FragPosition);
    float diff = max(dot(scaledNormal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;
    return diffuse * objectColor;
}

vec3 getPointLighting()
{
    return vec3(0.0);
}
