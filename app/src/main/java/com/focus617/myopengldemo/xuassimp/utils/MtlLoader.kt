package com.focus617.myopengldemo.xuassimp.utils

import android.content.Context
import android.text.TextUtils
import com.focus617.myopengldemo.xuassimp.base.Material
import timber.log.Timber

/**
 * @description Wavefront Obj 3D模型之mtl文件解析类
 */
object MtlLoader {

    var mtlMap: HashMap<String, Material>? = null      // 全部材质列表

    /**
     * 加载并分析 mtl文件，构造 MtlInfo
     * @param context   Context
     * @param mtlFileName assets的 mtl文件路径
     * @return
     */
    fun load(context: Context, mtlFileName: String) {
        if (mtlFileName.isEmpty() or TextUtils.isEmpty(mtlFileName)) {
            Timber.w("Mtl File doesn't exist")
            return
        }

        loadFromMtlFile(context, mtlFileName)

    }

    private fun loadFromMtlFile(context: Context, mtlFileName: String) {

    }

    private fun fillMtlMap(){

    }


    /**
     * 材质需解析字段
     */
    // 定义一个名为 'xxx'的材质
    private const val NEWMTL = "newmtl"

    // 材质的环境光（ambient color）
    private const val KA = "Ka"

    // 散射光（diffuse color）用Kd
    private const val KD = "Kd"

    // 镜面光（specular color）用Ks
    private const val KS = "Ks"

    // 反射指数 定义了反射高光度。该值越高则高光越密集，一般取值范围在0~1000。
    private const val NS = "Ns"

    // 渐隐指数描述 参数factor表示物体融入背景的数量，取值范围为0.0~1.0，取值为1.0表示完全不透明，取值为0.0时表示完全透明。
    private const val D = "d"

    // 滤光透射率
    private const val TR = "Tr"

    // map_Ka，map_Kd，map_Ks：材质的环境（ambient），散射（diffuse）和镜面（specular）贴图
    private const val MAP_KA = "map_Ka"
    private const val MAP_KD = "map_Kd"
    private const val MAP_KS = "map_Ks"
    private const val MAP_NS = "map_Ns"
    private const val MAP_D = "map_d"
    private const val MAP_TR = "map_Tr"
    private const val MAP_BUMP = "map_Bump"


}