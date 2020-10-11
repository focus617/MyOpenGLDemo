#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

in vec3 v_worldSpacePos;
in vec3 v_worldSpaceViewPos;
in vec3 v_Normal;
in vec2 v_TexCoords;

out vec4 gl_FragColor;

struct Material {
    sampler2D diffuse;
    // 镜面强度(Specular Intensity)
    vec3 specular;
    // 高光的反光度
    float shininess;
};

uniform Material material;

struct Light {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform Light light;

vec3 norm;
vec3 lightDir;
float lightDistance;

vec3 viewDir;
vec3 reflectDir;

vec3 getAmbientLighting();
vec3 getDiffuseLighting();
vec3 getSpecularLighting();

void main()
{

    // 在世界坐标空间中计算 着色点到点光源的距离 和 光照方向
    lightDir = vec3(light.position - v_worldSpacePos);
    lightDistance = length(lightDir);

    // 把法线和最终的方向向量都进行标准化
    norm = normalize(v_Normal);
    lightDir = normalize(lightDir);

    // 计算视线方向向量，和对应的沿着法线轴的反射向量
    viewDir = normalize(v_worldSpaceViewPos - v_worldSpacePos);
    reflectDir = reflect(-lightDir, norm);

    vec3 result = getAmbientLighting();
    result += getDiffuseLighting();
    result += getSpecularLighting();

    gl_FragColor = vec4(result, 1.0);
}

// 环境光
vec3 getAmbientLighting()
{
    //vec3 ambient = light.ambient * material.ambient;
    vec3 ambient = light.ambient * vec3(texture(material.diffuse, v_TexCoords));

    return ambient;
}

// 漫反射
vec3 getDiffuseLighting()
{
    float adjustParam = 50.0;
    float cosine = max(dot(norm, lightDir), 0.0);
    float diff = cosine * adjustParam / (pow(lightDistance, 2.0));

    //vec3 diffuse = light.diffuse * diff * material.diffuse;
    vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, v_TexCoords));

    return diffuse;
}

// 镜面光
vec3 getSpecularLighting()
{
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * (spec * material.specular);

    return specular;
}

