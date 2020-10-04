#version 300 es
#ifdef GL_ES
precision highp float;
#else
precision mediump float;
#endif

uniform sampler2D u_TextureUnit;
in vec3 v_Color;
in float v_ElapsedTime;

out vec4 gl_FragColor;

void main()
{
    /*
    float xDistance = 0.5 - gl_PointCoord.x;
    float yDistance = 0.5 - gl_PointCoord.y;
    float distanceFromCenter =
        sqrt(xDistance * xDistance + yDistance * yDistance);
    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);

    if (distanceFromCenter > 0.5) {
        discard;
    } else {
        gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);
    }
    */
    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0) * texture(u_TextureUnit, gl_PointCoord);
}

