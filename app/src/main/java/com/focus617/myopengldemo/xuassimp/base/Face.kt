package com.focus617.myopengldemo.xuassimp.base

import java.util.ArrayList

class FaceElement(var vId: Int, var tId: Int, var nId: Int) {
    override fun toString() = "($vId, $tId, $nId)"
}

/**
 * 对应于Assimp的 Face类
 */
class Face() {
    var fileName: String = ""
    val mIndices = ArrayList<FaceElement>()

    fun add(index: FaceElement) =  mIndices.add(index)

    override fun toString() = "$fileName, $mIndices"
}