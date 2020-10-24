package com.focus617.myopengldemo.utils

class Vector(var x: Float, var y: Float, var z: Float) {

    constructor(from: Geometry.Point, to: Geometry.Point) :
            this((to.x - from.x), (to.y - from.y), (to.z - from.z))

    constructor(from: Vector, to: Vector) :
            this((to.x - from.x), (to.y - from.y), (to.z - from.z))


    override fun toString(): String = "($x, $y, $z)"
    fun toFloatArray() = floatArrayOf(x,y,z)

    //求向量的模的方法
    fun module(): Float = kotlin.math.sqrt(x * x + y * y + z * z)

    //向量规格化的方法
    fun normalize(): Vector = scale(1f / module())

    //求向量叉积的方法
    // http://en.wikipedia.org/wiki/Cross_product
    fun crossProduct(other: Vector) = Vector(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    // http://en.wikipedia.org/wiki/Dot_product
    fun dotProduct(other: Vector): Float =
        x * other.x + y * other.y + z * other.z

    fun plus(other: Vector) = Vector(
        x + other.x,
        y + other.y,
        z * other.z
    )

    fun scale(f: Float) = Vector(
        x * f,
        y * f,
        z * f
    )


    companion object{

        // http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
        // Note that this formula treats Ray as if it extended infinitely past
        // either point.
        fun distanceBetween(point: Geometry.Point, ray: Geometry.Ray): Float {
            val p1ToPoint: Vector = vectorBetween(ray.point, point)
            val p2ToPoint: Vector =
                vectorBetween(ray.point.translate(ray.vector), point)

            // The length of the cross product gives the area of an imaginary
            // parallelogram having the two vectors as sides. A parallelogram can be
            // thought of as consisting of two triangles, so this is the same as
            // twice the area of the triangle defined by the two vectors.
            // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
            val areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).module()
            val lengthOfBase = ray.vector.module()

            // The area of a triangle is also equal to (base * height) / 2. In
            // other words, the height is equal to (area * 2) / base. The height
            // of this triangle is the distance from the point to the ray.
            return areaOfTriangleTimesTwo / lengthOfBase
        }

        fun vectorBetween(from: Geometry.Point, to: Geometry.Point): Vector {
            return Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z
            )
        }

        // 相交测试
        fun intersects(sphere: Geometry.Sphere, ray: Geometry.Ray): Boolean {
            return distanceBetween(sphere.center, ray) < sphere.radius
        }

        // http://en.wikipedia.org/wiki/Line-plane_intersection
        // This also treats rays as if they were infinite. It will return a
        // point full of NaNs if there is no intersection point.
        fun intersectionPoint(ray: Geometry.Ray, plane: Geometry.Plane): Geometry.Point {
            val rayToPlaneVector: Vector = vectorBetween(ray.point, plane.point)

            val scaleFactor = (rayToPlaneVector.dotProduct(plane.normal)
                    / ray.vector.dotProduct(plane.normal))

            return ray.point.translate(ray.vector.scale(scaleFactor))
        }

        /**
         * 计算圆锥面指定棱顶点法向量的方法
         */
        fun calConeNormal(
            x0: Float, y0: Float, z0: Float,  //A，中心点(底面圆的圆心)
            x1: Float, y1: Float, z1: Float,  //B，底面圆上的某一点
            x2: Float, y2: Float, z2: Float   //C，圆锥中心最高点
        ): Vector {
            val a = Vector(x1 - x0, y1 - y0, z1 - z0) //向量AB
            val b = Vector(x2 - x0, y2 - y0, z2 - z0) //向量AC
            val c = Vector(x2 - x1, y2 - y1, z2 - z1) //向量BC
            //先求平面ABC的法向量k
            val k = a.crossProduct(b)
            //将c和k做叉乘，得出所求向量d
            val d = c.crossProduct(k)
            //返回规格化后的法向量
            return d.normalize()
        }

        /**
         * 计算三角形的法向量
         * 通过三角形面两个边向量 0-1，0-2, 求叉积得到此面的法向
         * */
        fun calTriangleNormal(
            x0: Float, y0: Float, z0: Float,  //A
            x1: Float, y1: Float, z1: Float,  //B
            x2: Float, y2: Float, z2: Float   //C
        ): Vector {
            val a = Vector(x1 - x0, y1 - y0, z1 - z0) //向量AB
            val b = Vector(x2 - x0, y2 - y0, z2 - z0) //向量AC

            //通过求两个向量的叉积计算出此三角形面的法向量
            val vNormal = a.crossProduct(b).normalize()
            return vNormal.normalize()
        }
    }


}
