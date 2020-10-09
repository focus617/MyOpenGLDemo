package com.focus617.myopengldemo.render

import android.content.Context
import android.opengl.GLES31
import android.opengl.GLES31.GL_COLOR_BUFFER_BIT
import android.opengl.GLES31.glClear
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.objects.airhockey.Mallet
import com.focus617.myopengldemo.objects.airhockey.Puck
import com.focus617.myopengldemo.objects.airhockey.Table
import com.focus617.myopengldemo.programs.airhockey.ColorShaderProgram
import com.focus617.myopengldemo.programs.airhockey.TextureShaderProgram
import com.focus617.myopengldemo.util.Geometry
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.Geometry.Ray
import com.focus617.myopengldemo.util.Geometry.Sphere
import com.focus617.myopengldemo.util.Geometry.Plane
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import com.focus617.myopengldemo.util.TextureHelper
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 拆分类之后（第7章），按照ES 3.0 VBO, VAO和 Element改进的实现
 */
class AirHockeyRendererEs3(val context: Context) : GLSurfaceView.Renderer {

    private val mModelMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)

    private val mViewProjectionMatrix = FloatArray(16)
    private val invertedViewProjectionMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture = 0

    // 通过触摸事件获取要求视图矩阵旋转的角度:记录上个事件时的旋转角度
    private var xRotation: Float = 0f
    private var yRotation: Float = 0f

    private var malletPressed = false
    private lateinit var blueMalletPosition: Point
    private lateinit var previousBlueMalletPosition: Point

    private val leftBound = -0.5f
    private val rightBound = 0.5f
    private val farBound = -0.8f
    private val nearBound = 0.8f

    // 冰球的速度和方向
    private lateinit var puckPosition: Point
    private lateinit var puckVector: Vector

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        // 设置重绘背景框架颜色
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        blueMalletPosition = Point(0f, mallet.height / 2f, 0.4f)
        puckPosition = Point(0f, puck.height / 2f, 0f)
        puckVector = Vector(0f, 0f, 0f)

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        // 设置渲染的OpenGL场景（视口）的位置和大小
        Timber.d("width = $width, height = $height")

        // Set the OpenGL viewport to fill the entire surface.
        GLES31.glViewport(0, 0, width, height)

        // 计算透视投影矩阵 (Project Matrix)，而后将应用于onDrawFrame（）方法中的对象坐标
        val aspect: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 7f)

        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        Matrix.setLookAtM(
            mViewMatrix, 0, 0f, 2.5f, 2.75f,
            0f, 0f, 0f, 0f, 1.0f, 0.0f
        )

    }

    override fun onDrawFrame(unused: GL10) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT)

        setupRotation()

        // 视图转换：Multiply the view and projection matrices together
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        // Create an inverted matrix for touch picking.
        Matrix.invertM(invertedViewProjectionMatrix, 0, mViewProjectionMatrix, 0)

        // Draw the table.
        positionTableInScene()
        textureProgram.useProgram()
        textureProgram.setUniforms(mMVPMatrix, texture)
        table.bindData()
        table.draw()

        // Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.5f)
        colorProgram.useProgram()
        colorProgram.setUniforms(mMVPMatrix, 1f, 0f, 0f)
        mallet.bindData()
        mallet.draw()

        positionObjectInScene(
            blueMalletPosition.x,
            blueMalletPosition.y,
            blueMalletPosition.z
        )
        colorProgram.setUniforms(mMVPMatrix, 0f, 0f, 1f)
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        mallet.draw()

        calculateObjectPosition()

        // Draw the puck.
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z)
        colorProgram.setUniforms(mMVPMatrix, 0.8f, 0.8f, 1f)
        puck.bindData()
        puck.draw()

    }

    private fun calculateObjectPosition() {
        // Translate the puck by its vector
        puckPosition = puckPosition.translate(puckVector)

        // If the puck struck a side, reflect it off that side.
        if (puckPosition.x < leftBound + puck.radius
            || puckPosition.x > rightBound - puck.radius
        ) {
            puckVector = Vector(-puckVector.x, puckVector.y, puckVector.z)
            puckVector = puckVector.scale(0.9f)
        }
        if (puckPosition.z < farBound + puck.radius
            || puckPosition.z > nearBound - puck.radius
        ) {
            puckVector = Vector(puckVector.x, puckVector.y, -puckVector.z)
            puckVector = puckVector.scale(0.9f)
        }

        // Clamp the puck position.
        puckPosition = Point(
            clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
            puckPosition.y,
            clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        )

        // Friction factor
        puckVector = puckVector.scale(0.99f)
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(mModelMatrix, 0)

        Matrix.translateM(mModelMatrix, 0, x, y, z)

        // 视图转换：计算模型视图投影矩阵MVPMatrix，该矩阵可以将模型空间的坐标转换为归一化设备空间坐标
        Matrix.multiplyMM(mMVPMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0)
    }

    private fun positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.rotateM(mModelMatrix, 0, -90f, 1f, 0f, 0f)

        Matrix.multiplyMM(
            mMVPMatrix, 0, mViewProjectionMatrix,
            0, mModelMatrix, 0
        )
    }

    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        Timber.d("handleTouchPress(): normalized Coord. = ($normalizedX,$normalizedY)")

        val ray: Ray = convertNormalized2DPointToRay(normalizedX, normalizedY)

        // Now test if this ray intersects with the mallet by creating a
        // bounding sphere that wraps the mallet.
        val malletBoundingSphere = Sphere(
            Point(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z
            ),
            mallet.height / 2f
        )

        // If the ray intersects (if the user touched a part of the screen that
        // intersects the mallet's bounding sphere), then set malletPressed =
        // true.
        malletPressed = Geometry.intersects(malletBoundingSphere, ray)
        Timber.d("handleTouchPress(): malletPressed = $malletPressed")
    }

    fun handleTouchDrag(normalizedX: Float, normalizedY: Float, deltaX: Float, deltaY: Float) {

        if (malletPressed) {
            Timber.d("handleTouchDrag()")

            val ray = convertNormalized2DPointToRay(normalizedX, normalizedY)

            // Define a plane representing our air hockey table.
            val plane = Plane(Point(0f, 0f, 0f), Vector(0f, 1f, 0f))

            // Find out where the touched point intersects the plane
            // representing our table. We'll move the mallet along this plane.
            val touchedPoint = Geometry.intersectionPoint(ray, plane)

            previousBlueMalletPosition = blueMalletPosition

            // Clamp to bounds
            blueMalletPosition = Point(
                clamp(
                    touchedPoint.x,
                    leftBound + mallet.radius,
                    rightBound - mallet.radius
                ),
                mallet.height / 2f,
                clamp(
                    touchedPoint.z,
                    // TODO: temp replace
                    //  0 + mallet.radius,
                    farBound + mallet.radius,
                    nearBound - mallet.radius
                )
            )

            // Now test if mallet has struck the puck.
            val distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length()
            if (distance < puck.radius + mallet.radius) {
                // The mallet has struck the puck. Now send the puck flying
                // based on the mallet velocity.
                puckVector = Geometry.vectorBetween(
                    previousBlueMalletPosition, blueMalletPosition
                )
            }
        } else {
            // MotionEvent报告触摸屏和其他输入控件的输入详细信息。
            // 在这种情况下，这里只对触摸位置发生变化的事件感兴趣。
            xRotation += deltaX / 16f
            yRotation += deltaY / 16f

            if (yRotation < -90) {
                yRotation = -90f
            } else if (yRotation > 90) {
                yRotation = 90f
            }
        }
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return kotlin.math.min(max, kotlin.math.max(value, min))
    }

    private fun convertNormalized2DPointToRay(
        normalizedX: Float, normalizedY: Float
    ): Ray {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        val nearPointNdc = floatArrayOf(normalizedX, normalizedY, -1f, 1f)
        val farPointNdc = floatArrayOf(normalizedX, normalizedY, 1f, 1f)

        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)

        Matrix.multiplyMV(
            nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0
        )
        Matrix.multiplyMV(
            farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0
        )

        // Why are we dividing by W? We multiplied our vector by an inverse
        // matrix, so the W value that we end up is actually the *inverse* of
        // what the projection matrix would create. By dividing all 3 components
        // by W, we effectively undo the hardware perspective divide.
        divideByW(nearPointWorld)
        divideByW(farPointWorld)

        // We don't care about the W value anymore, because our points are now
        // in world coordinates.
        val nearPointRay = Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2])
        val farPointRay = Point(farPointWorld[0], farPointWorld[1], farPointWorld[2])

        return Ray(
            nearPointRay,
            Geometry.vectorBetween(nearPointRay, farPointRay)
        )
    }

    private fun divideByW(vector: FloatArray) {
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }

    /**
     * 在 SurfaceView中通过触摸事件获取到要视图矩阵旋转的角度
     * 由于渲染器代码在与应用程序的主用户界面线程在不同的线程上运行，因此必须将此公共变量声明为volatile。
     */
    @Volatile
    var mAngle = 0f

    private fun getAngle(): Float {
        return mAngle
    }

    private fun setAngle(angle: Float) {
        mAngle = angle
    }

    // 处理旋转
    private fun setupRotation() {

        // 进行旋转变换
        //Matrix.rotateM(mViewMatrix, 0, getAngle(), 0f, 1.0f, 0f)

        if(xRotation<yRotation)
            Matrix.rotateM(mViewMatrix, 0, yRotation, 1f, 0f, 0f)
        else
            Matrix.rotateM(mViewMatrix, 0, xRotation, 0f, 1f, 0f)

        xRotation = 0f
        yRotation = 0f
    }


}