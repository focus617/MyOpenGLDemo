package com.focus617.myopengldemo.xuscene.base

import timber.log.Timber

/**
 * mtl文件格式 和 解析后的信息类
 *
 * newmtl Default       # 定义一个名为 'Default'的材质
 *
 * # exponent指定材质的反射指数，定义了反射高光度
 * Ns 96.078431
 * # 材质的环境光
 * Ka 0 0 0
 * # 散射光
 * Kd 0.784314 0.784314 0.784314
 * # 镜面光
 * Ks 0 0 0
 *
 * # 透明度
 * d 1
 *
 * # 为漫反射指定颜色纹理文件
 * map_Kd test_vt.png
 * map_Ka picture1.png #阴影纹理贴图
 * map_Ks picture2.png #高光纹理贴图
 * illum 2 #光照模型
 *
 * #光照模型属性如下：
 * #0. 色彩开，阴影色关
 * #1. 色彩开，阴影色开
 * #2. 高光开
 * #3. 反射开，光线追踪开
 * #4. 透明： 玻璃开 反射：光线追踪开
 * #5. 反射：菲涅尔衍射开，光线追踪开
 * #6. 透明：折射开 反射：菲涅尔衍射关，光线追踪开
 * #7. 透明：折射开 反射：菲涅尔衍射开，光线追踪开
 * #8. 反射开，光线追踪关
 * #9. 透明： 玻璃开 反射：光线追踪关
 * #10. 投射阴影于不可见表面
 *
 * mtl文件信息类
 * 对应于Assimp的 Material类
 */
class Material {
    // 材质对象名称
    var name: String? = null

    // 环境光
    var Ka_Color = 0

    // 散射光
    var Kd_Color = 0

    // 镜面光
    var Ks_Color = 0

    // 高光调整参数
    var ns = 0f

    // 溶解度，为0时完全透明，1完全不透明
    var alpha = 1f

    // 材质的光照模型
    var illum = 0

    // map_Ka，map_Kd，map_Ks：材质的环境（ambient），散射（diffuse）和镜面（specular）贴图
    var Ka_Texture: String? = null
    var Kd_Texture: String? = null
    var Ks_ColorTexture: String? = null
    var Ns_Texture: String? = null
    var alphaTexture: String? = null
    var bumpTexture: String? = null

    fun dump() {
        Timber.d("MtlName: $name")
        Timber.d("Ka_Color: $Ka_Color")
        Timber.d("Kd_Color: $Kd_Color")
        Timber.d("Ks_Color: $Ks_Color")
        Timber.d("ns: $ns")
        Timber.d("alpha: $alpha")
        Timber.d("illum: $illum")

        if(Ka_Texture!=null) Timber.d("Ka_Texture: $Ka_Texture")
        if(Kd_Texture!=null) Timber.d("Kd_Texture: $Kd_Texture")
        if(Ks_ColorTexture!=null) Timber.d("Ks_ColorTexture: $Ks_ColorTexture")
        if(Ns_Texture!=null) Timber.d("Ns_Texture: $Ns_Texture")
        if(alphaTexture!=null) Timber.d("alphaTexture: $alphaTexture")
        if(bumpTexture!=null) Timber.d("bumpTexture: $bumpTexture")
    }
}