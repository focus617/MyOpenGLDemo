package com.focus617.myopengldemo.xuassimp.utils

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import com.focus617.myopengldemo.xuassimp.base.Material
import com.focus617.myopengldemo.xuassimp.data.XuScene
import timber.log.Timber
import java.util.*

/**
 * @description Wavefront Obj 3D模型之mtl文件解析类
 */
object MtlLoader {

//    var mMtlMap = XuScene.mMaterials      // 全部材质列表

    private var currentMaterial: Material? = null

    /**
     * 加载并分析 mtl文件，并将结果存入 XuScene的材质列表 [XuScene.mMaterials]
     * @param context   Context
     * @param mtlFileName assets的 mtl文件路径
     * @return
     */
    fun load(context: Context, mtlFileName: String) {

        Timber.d("load() From Mtl File: $mtlFileName")
        if (mtlFileName.isEmpty() or TextUtils.isEmpty(mtlFileName)) {
            Timber.w("Mtl File doesn't exist")
            return
        }

        try {
            val scanner = Scanner(context.assets.open(mtlFileName))
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()

                when {
                    line.isEmpty() -> continue

                    line.startsWith(ANNOTATION) -> continue // 注释行

                    line.startsWith(NEWMTL) -> {            // 定义一个名为 'xxx'的材质
                        fillNewMTL(line)
                    }
                    line.startsWith(KA) -> {                // 环境光
                        currentMaterial!!.Ka_Color = getColorFromLine(line)
                    }
                    line.startsWith(KD) -> {                // 散射光
                        currentMaterial!!.Kd_Color = getColorFromLine(line)
                    }
                    line.startsWith(KS) -> {                // 镜面光
                        currentMaterial!!.Ks_Color = getColorFromLine(line)
                    }
                    line.startsWith(NS) -> {                // 高光调整参数
                        currentMaterial!!.ns = getFloatFromLine(line)
                    }
                    line.startsWith(D) -> {                // 高光调整参数
                        currentMaterial!!.alpha = getFloatFromLine(line)
                    }
                    line.startsWith(ILLUM) -> {                // 高光调整参数
                        currentMaterial!!.illum = getIntFromLine(line)
                    }
                    line.startsWith(MAP_KA) -> {            //材质的环境贴图
                        currentMaterial!!.Ka_Texture = getStringFromLine(line)
                    }
                    line.startsWith(MAP_KD) -> {            //材质的散射贴图
                        currentMaterial!!.Kd_Texture = getStringFromLine(line)
                    }
                    line.startsWith(MAP_KS) -> {            //材质的镜面贴图
                        currentMaterial!!.Ks_ColorTexture = getStringFromLine(line)
                    }
                    line.startsWith(MAP_NS) -> {
                        currentMaterial!!.Ns_Texture = getStringFromLine(line)
                    }
                    line.startsWith(MAP_D) || line.startsWith(MAP_TR) -> {
                        currentMaterial!!.alphaTexture = getStringFromLine(line)
                    }
                    line.startsWith(MAP_BUMP) -> {
                        currentMaterial!!.bumpTexture = getStringFromLine(line)
                    }

                    else -> {
                        Timber.d("dropped: $line")
                    }
                }
            }

            scanner.close()
        } catch (ex: Exception) {
            Timber.e(ex.message.toString())
        }
        // 将最后一个材质保存
        XuScene.mMaterials.set(currentMaterial!!.name!!, currentMaterial!!)
        Timber.d("MTL: ${currentMaterial!!.name!!} closed")
    }

    private fun fillNewMTL(line: String) {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 2) return

        if (currentMaterial != null) {
            // 开始定义一个新的材质，因此要将上一个材质存入 mMtlMap
            XuScene.mMaterials[currentMaterial!!.name!!] = currentMaterial!!
            Timber.d("MTL: ${currentMaterial!!.name!!} closed")
        }
        currentMaterial = Material()
        currentMaterial!!.name = items[1]
        Timber.d("Create new mtl: ${currentMaterial!!.name!!} ")
    }

    private fun fillNs(line: String) {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 2) return

        currentMaterial!!.ns = items[1].toFloat()
    }


    private const val DELIMITER = " "    // 分隔符

    /**
     * 材质需解析字段
     */
    // 注释
    private const val ANNOTATION = "#"

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

    // 材质的光照模型
    private const val ILLUM = "illum"

    // map_Ka，map_Kd，map_Ks：材质的环境（ambient），散射（diffuse）和镜面（specular）贴图
    private const val MAP_KA = "map_Ka"
    private const val MAP_KD = "map_Kd"
    private const val MAP_KS = "map_Ks"
    private const val MAP_NS = "map_Ns"
    private const val MAP_D = "map_d"
    private const val MAP_TR = "map_Tr"
    private const val MAP_BUMP = "map_Bump"

    /**
     * 返回一个0xffffffff格式的颜色值
     *
     * @param parts
     * @return
     */
    private fun getColorFromLine(line: String): Int {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 4) return Color.rgb(0, 0, 0)

        val r = (items[1].toFloat() * 255f).toInt()
        val g = (items[2].toFloat() * 255f).toInt()
        val b = (items[3].toFloat() * 255f).toInt()
        return Color.rgb(r, g, b)
    }

    private fun getFloatFromLine(line: String): Float {
        val items = line.split(DELIMITER).toTypedArray()
        return if (items.size == 2) items[1].toFloat() else 0f
    }

    private fun getIntFromLine(line: String): Int {
        val items = line.split(DELIMITER).toTypedArray()
        return if (items.size == 2) items[1].toInt() else 0
    }

    private fun getStringFromLine(line: String): String {
        val items = line.split(DELIMITER).toTypedArray()
        return if (items.size == 2) items[1] else ""
    }
}