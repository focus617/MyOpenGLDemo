#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform sampler2D u_TextureUnit1;
uniform sampler2D u_TextureUnit2;

uniform float uR;
in vec3 v_Position;//接收从顶点着色器过来的参数

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

vec3 material;

vec3 norm;
vec3 lightDir;
float lightDistance;

vec3 viewDir;
vec3 reflectDir;

vec3 getMaterialColor();
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

    vec3 ColorDay;//从白天纹理中采样出颜色值
    vec3 ColorNight;//从夜晚纹理中采样出颜色值

    ColorDay= vec3(texture(u_TextureUnit1, v_TexCoords));//采样出白天纹理的颜色值
    ColorNight = vec3(texture(u_TextureUnit2, v_TexCoords));//采样出夜晚纹理的颜色值

    vec3 finalColorDay = ambient*ColorDay + diffuse*ColorDay + specular*ColorDay;
    vec3 finalColorNight = ColorNight * vec3(0.5, 0.5, 0.5);

    vec3 result;
    if (diffuse.x>0.21)
    { //当散射光分量大于0.21时, 采用白天纹理
        result = finalColorDay;
    }
    else if (diffuse.x<0.05)
    { //当散射光分量小于0.05时, 采用夜间纹理
        result = finalColorNight;//计算出的该片元夜晚颜色值;
    }
    else
    { //当环境光分量大于0.05小于0.21时，为白天夜间纹理的过渡阶段
        float t=(diffuse.x-0.05)/0.16;//计算白天纹理应占纹理过渡阶段的百分比
        result = t*finalColorDay + (1.0-t)*finalColorNight;//计算白天黑夜过渡阶段的颜色值
    }

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



