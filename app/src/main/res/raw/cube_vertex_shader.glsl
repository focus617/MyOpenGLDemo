#version 300 es
layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_IT_MVMatrix;

uniform vec3 u_ViewPos;
uniform vec3 u_worldSpaceLightPos;

uniform vec3 u_PointLightColor;
uniform vec3 u_MaterialColor;

out vec3 v_Color;

vec3 norm;
vec3 lightDir;
float lightDistance;

vec3 viewDir;
vec3 reflectDir;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

uniform Material material;

vec3 getAmbientLighting();
vec3 getDiffuseLighting();
vec3 getSpecularLighting();

void main()
{
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0);

    // 将着色点和摄像机转换到世界坐标空间
    vec3 worldSpacePos = vec3(u_ModelMatrix * vec4(a_Position, 1.0));
    vec3 worldSpaceViewPos = vec3(u_ViewMatrix * vec4(u_ViewPos, 1.0));

    // 在世界坐标空间中计算 着色点到点光源的距离 和 光照方向
    lightDir = vec3(u_worldSpaceLightPos - worldSpacePos);
    lightDistance = length(lightDir);

    // 把法线和最终的方向向量都进行标准化
    norm = normalize(a_Normal);
    lightDir = normalize(lightDir);

    // 计算视线方向向量，和对应的沿着法线轴的反射向量
    viewDir = normalize(worldSpaceViewPos - worldSpacePos);
    reflectDir = reflect(-lightDir, norm);

    vec3 result = getAmbientLighting();
    result += getDiffuseLighting();
    result += getSpecularLighting();

    v_Color = result * u_MaterialColor;
}

// 环境光
vec3 getAmbientLighting()
{
    float ambientStrength = 0.1;
    vec3 ambient = u_PointLightColor * ambientStrength;

    return ambient;
}

// 漫反射
vec3 getDiffuseLighting()
{
    float cosine = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = u_PointLightColor * cosine * 25.0 / (pow(lightDistance, 2.0));
    //      u_PointLightColor * (cosine * material.diffuse) / (pow(lightDistance, 2.0));

    return diffuse;
}

// 镜面光
vec3 getSpecularLighting()
{
    // 镜面强度(Specular Intensity)
    float specularStrength = 0.5;
    // 高光的反光度
    float shininess = 132.0;

    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = u_PointLightColor * spec * specularStrength;

    return specular;
}
