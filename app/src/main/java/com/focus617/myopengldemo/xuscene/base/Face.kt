package com.focus617.myopengldemo.xuscene.base

import java.util.ArrayList

class FaceElement(var vId: Int, var tId: Int, var nId: Int) {
    override fun toString() = "($vId, $tId, $nId)"
}

/**
 * 对应于Assimp的 Face类
 */
class Face() {
    val mIndices = ArrayList<FaceElement>()

    fun add(index: FaceElement) =  mIndices.add(index)

    override fun toString() = "$mIndices"
}