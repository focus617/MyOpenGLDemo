#version 300 es
layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_IT_MVMatrix;

uniform vec3 u_worldSpaceLightPos;
uniform vec3 u_PointLightColor;
uniform vec3 u_MaterialColor;

out vec3 v_Color;

vec3 eyeSpaceNormal;
vec3 eyeSpaceLightDir;
float eyeSpaceLightDistance;

vec3 getAmbientLighting();
vec3 getPointLighting();
vec3 getDirectionalLighting();

void main()
{
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0f);

    mat4 ModelViewMatrix = u_ViewMatrix * u_ModelMatrix;

    // 将着色点转换到视图空间坐标
    vec3 eyeSpacePosition = vec3(ModelViewMatrix * vec4(a_Position, 1.0f));

    // 将点光源转换到视图空间坐标
    vec3 eyeSpaceLightPos = vec3(ModelViewMatrix * vec4(u_worldSpaceLightPos, 1.0f));

    // 在EyeSpace中计算 着色点到点光源的距离 和 光照方向
    eyeSpaceLightDir = vec3(eyeSpaceLightPos - eyeSpacePosition);
    eyeSpaceLightDistance = length(eyeSpaceLightDir);
    eyeSpaceLightDir = normalize(eyeSpaceLightDir);

    // The model normals need to be adjusted as per the transpose
    // of the inverse of the modelview matrix.
    eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));

    vec3 result = getAmbientLighting();
    result += getDirectionalLighting();
    result += getPointLighting();

    v_Color = result * u_MaterialColor;
}

vec3 getAmbientLighting()
{
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * u_PointLightColor;
    return ambient;
}

vec3 getPointLighting()
{

    float cosine = max(dot(eyeSpaceNormal, eyeSpaceLightDir), 0.0);
    vec3 diffuse = cosine * u_PointLightColor * 50.0 / (eyeSpaceLightDistance*eyeSpaceLightDistance);
    return diffuse;
}

vec3 getDirectionalLighting()
{
    return vec3(0.0);
}
