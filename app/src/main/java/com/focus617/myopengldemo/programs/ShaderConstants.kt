package com.focus617.myopengldemo.programs

object ShaderConstants {
    // Uniform constants
    const val U_MATRIX = "u_MVPMatrix"
    const val U_COLOR = "u_Color"
    const val U_TEXTURE_UNIT = "u_TextureUnit"
    const val U_TEXTURE_UNIT_1 = "u_TextureUnit1"
    const val U_TEXTURE_UNIT_2 = "u_TextureUnit2"
    const val U_TIME = "u_Time"
    const val U_VECTOR_TO_LIGHT = "u_VectorToLight"
    const val U_MV_MATRIX = "u_MVMatrix"
    const val U_IT_MV_MATRIX = "u_IT_MVMatrix"
    const val U_POINT_LIGHT_POSITIONS = "u_PointLightPositions"
    const val U_POINT_LIGHT_COLORS = "u_PointLightColors"

    // Attribute constants
    const val A_POSITION = "a_Position"
    const val A_COLOR = "a_Color"
    const val A_NORMAL = "a_Normal"
    const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    const val A_DIRECTION_VECTOR = "a_DirectionVector"
    const val A_PARTICLE_START_TIME = "a_ParticleStartTime"
}