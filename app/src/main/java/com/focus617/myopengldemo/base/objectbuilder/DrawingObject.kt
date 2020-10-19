package com.focus617.myopengldemo.base.objectbuilder

interface DrawingObject{
    fun bindData()

    // 绘制方法
    fun draw()
}

interface NewDrawingObject{
    //初始化Shader Program
    fun initShader()

    //初始化顶点数据的方法
    fun initVertexArray()

    fun bindData()

    // 绘制方法
    fun draw()
}