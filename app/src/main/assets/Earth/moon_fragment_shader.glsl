#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform sampler2D u_TextureUnit;

in vec3 v_worldSpacePos;
in vec3 v_worldSpaceViewPos;
in vec3 v_Normal;
in vec2 v_TexCoords;

out vec4 gl_FragColor;//输出到的片元颜色

struct Light {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
// 衰减参数
    float constant;
    float linear;
    float quadratic;
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

    vec3 ambient = getAmbientLighting();
    vec3 diffuse = getDiffuseLighting();
    vec3 specular = getSpecularLighting();

    //纹理采样颜色值
    vec3 moonColor = vec3(texture(u_TextureUnit, v_TexCoords));

    vec3 result = ambient*moonColor + diffuse*moonColor + specular*moonColor;

    //将计算出的颜色传递给管线
    gl_FragColor = vec4(result, 1.0);
}

// 环境光
vec3 getAmbientLighting()
{
    vec3 ambient = light.ambient;

    return ambient;
}

// 漫反射
vec3 getDiffuseLighting()
{
    float adjustParam = 5.0;
    float cosine = max(dot(norm, lightDir), 0.0);
    float attenuation = 1.0 / (light.constant + light.linear * lightDistance +
    light.quadratic * (pow(lightDistance, 2.0)));
    float diff = cosine * adjustParam * attenuation;

    vec3 diffuse = light.diffuse * diff;

    return diffuse;
}

// 镜面光
vec3 getSpecularLighting()
{
    float  shininess = 100.0;// 高光的反光度

    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = light.specular * spec;

    return specular;
}



