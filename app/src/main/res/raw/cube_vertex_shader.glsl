#version 300 es
layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_IT_MVMatrix;

uniform vec3 u_worldSpaceViewPos;
uniform vec3 u_worldSpaceLightPos;
uniform vec3 u_PointLightColor;
uniform vec3 u_MaterialColor;

out vec3 v_Color;

vec3 eyeSpaceNormal;
vec3 eyeSpaceLightDir;
float eyeSpaceLightDistance;

vec3 eyeSpaceViewDir;
vec3 eyeSpaceReflectDir;

vec3 getAmbientLighting();
vec3 getDiffuseLighting();
vec3 getSpecularLighting();

void main()
{
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0f);

    // 将着色点转换到视图空间坐标
    vec3 eyeSpacePosition = vec3(u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0f));

    // 将点光源转换到视图空间坐标
    vec3 eyeSpaceLightPos = vec3(u_ViewMatrix * vec4(u_worldSpaceLightPos, 1.0f));

    // 将观察点转换到视图空间坐标
    vec3 eyeSpaceViewPos = vec3(u_ViewMatrix * vec4(u_worldSpaceViewPos, 1.0f));

    // 在EyeSpace中计算 着色点到点光源的距离 和 光照方向
    eyeSpaceLightDir = vec3(eyeSpaceLightPos - eyeSpacePosition);
    eyeSpaceLightDistance = length(eyeSpaceLightDir);
    eyeSpaceLightDir = normalize(eyeSpaceLightDir);

    // The model normals need to be adjusted as per the transpose
    // of the inverse of the modelview matrix.
    eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));

    // 计算视线方向向量，和对应的沿着法线轴的反射向量
    eyeSpaceViewDir = normalize(eyeSpaceViewPos - eyeSpacePosition);
    eyeSpaceReflectDir = reflect(-eyeSpaceLightDir, eyeSpaceNormal);

    vec3 result = getAmbientLighting();
    result += getDiffuseLighting();
    result += getSpecularLighting();

    v_Color = result * u_MaterialColor;
}

vec3 getAmbientLighting()
{
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * u_PointLightColor;

    return ambient;
}

vec3 getDiffuseLighting()
{

    float cosine = max(dot(eyeSpaceNormal, eyeSpaceLightDir), 0.0);
    vec3 diffuse = u_PointLightColor * cosine * 50.0 / (eyeSpaceLightDistance*eyeSpaceLightDistance);

    return diffuse;
}

vec3 getSpecularLighting()
{
    // 镜面强度(Specular Intensity)
    float specularStrength = 0.5;
    // 高光的反光度
    float shininess = 150.0;

    float spec = pow(max(dot(eyeSpaceViewDir, eyeSpaceReflectDir), 0.0), shininess);
    vec3 specular = u_PointLightColor * specularStrength * spec;

    return specular;
}
