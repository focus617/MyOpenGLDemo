#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform float uR;
in vec3 v_Position;        //接收从顶点着色器过来的参数

in vec3 v_worldSpacePos;
in vec3 v_worldSpaceViewPos;
in vec3 v_Normal;
in vec2 v_TexCoords;

out vec4 gl_FragColor;      //输出到的片元颜色

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
    material = getMaterialColor();

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

    //将计算出的颜色传递给管线
    gl_FragColor = vec4(result, 1.0);
}

// 环境光
vec3 getAmbientLighting()
{
    vec3 ambient = light.ambient * material;
//    vec3 ambient = light.ambient * vec3(texture(material.diffuse, v_TexCoords));

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

    vec3 diffuse = light.diffuse * diff * material;
//    vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, v_TexCoords));

    return diffuse;
}

// 镜面光
vec3 getSpecularLighting()
{
    float  shininess = 100.0;   // 高光的反光度

    float spec = pow(max(dot(viewDir, reflectDir), 0.0),  shininess);
    vec3 specular = light.specular * (spec * material);

    return specular;
}


vec3 getMaterialColor()
{
    vec3 color;
    float n = 8.0;//外接立方体每个坐标轴方向切分的份数
    float span = 2.0*uR/n;//每一份的尺寸（小方块的边长）
    int i = int((v_Position.x + uR)/span);//当前片元位置小方块的行数
    int j = int((v_Position.y + uR)/span);//当前片元位置小方块的层数
    int k = int((v_Position.z + uR)/span);//当前片元位置小方块的列数

    //计算当前片元行数、层数、列数的和并对2取模
    int whichColor = int(mod(float(i+j+k), 2.0));
    if (whichColor == 1) { //奇数时为红色
        color = vec3(0.678, 0.231, 0.129);//红色
    }
    else { //偶数时为白色
        color = vec3(1.0, 1.0, 1.0);//白色
    }
    return color;
}


