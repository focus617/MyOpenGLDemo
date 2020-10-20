#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform float uR;
in vec2 mcLongLat;
in vec3 v_Position;        //接收从顶点着色器过来的参数

out vec4 gl_FragColor;      //输出到的片元颜色

void main()
{
    vec3 color;
    float n = 8.0;                          //外接立方体每个坐标轴方向切分的份数
    float span = 2.0*uR/n;                  //每一份的尺寸（小方块的边长）
    int i = int((v_Position.x + uR)/span);   //当前片元位置小方块的行数
    int j = int((v_Position.y + uR)/span);   //当前片元位置小方块的层数
    int k = int((v_Position.z + uR)/span);   //当前片元位置小方块的列数

    //计算当前片元行数、层数、列数的和并对2取模
    int whichColor = int(mod(float(i+j+k),2.0));
    if(whichColor == 1) {//奇数时为红色
        color = vec3(0.678,0.231,0.129);//红色
    }
    else {//偶数时为白色
        color = vec3(1.0,1.0,1.0);//白色
    }

    //将计算出的颜色传递给管线
    gl_FragColor = vec4(color,0);
}
