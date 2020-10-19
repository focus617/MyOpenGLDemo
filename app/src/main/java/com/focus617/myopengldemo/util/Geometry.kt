package com.focus617.myopengldemo.util

class Geometry {

    class Point(val x: Float, val y: Float, val z: Float) {

        fun translateY(distance: Float): Point {
            return Point(x, y + distance, z)
        }

        fun translate(vector: Vector): Point {
            return Point(
                x + vector.x,
                y + vector.y,
                z + vector.z
            )
        }
    }

    // 二维
    class Circle(val center: Point, val radius: Float) {

        fun scale(scale: Float): Circle {
            return Circle(center, radius * scale)
        }
    }

    // 三维
    class Ray(val point: Point, val vector: Vector)

    // 顶点顺序按逆时针
    class Triangle(private val point0: Point, private val point1: Point, private val point2: Point){
        // 三角形构成的平面的法线
        fun normal()  =
            Vector(point0, point1).crossProduct(Vector(point0, point2)).normalize()

    }

    class Cylinder(val center: Point, val radius: Float, val height: Float)

    class Sphere(val center: Point, val radius: Float)

    class Plane(val point: Point, val normal: Vector)

    companion object {

        class Vector(var x: Float, var y: Float, var z: Float) {

            constructor(from: Point, to: Point) :
                    this((to.x-from.x), (to.y-from.y),(to.z-from.z))

            constructor(from: Vector, to: Vector) :
                    this((to.x-from.x), (to.y-from.y),(to.z-from.z))

            override fun toString(): String = "($x, $y, $z)"

            fun length(): Float = kotlin.math.sqrt(x * x + y * y + z * z)

            // http://en.wikipedia.org/wiki/Cross_product
            fun crossProduct(other: Vector) = Vector(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
            )

            // http://en.wikipedia.org/wiki/Dot_product
            fun dotProduct(other: Vector): Float = x * other.x + y * other.y + z * other.z

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

            fun normalize(): Vector = scale(1f / length())
        }

        fun vectorBetween(from: Point, to: Point): Vector {
            return Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z
            )
        }

        // 相交测试
        fun intersects(sphere: Sphere, ray: Ray): Boolean {
            return Geometry.distanceBetween(sphere.center, ray) < sphere.radius
        }

        // http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
        // Note that this formula treats Ray as if it extended infinitely past
        // either point.
        fun distanceBetween(point: Point, ray: Ray): Float {
            val p1ToPoint: Vector = vectorBetween(ray.point, point)
            val p2ToPoint: Vector = vectorBetween(ray.point.translate(ray.vector), point)

            // The length of the cross product gives the area of an imaginary
            // parallelogram having the two vectors as sides. A parallelogram can be
            // thought of as consisting of two triangles, so this is the same as
            // twice the area of the triangle defined by the two vectors.
            // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
            val areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length()
            val lengthOfBase = ray.vector.length()

            // The area of a triangle is also equal to (base * height) / 2. In
            // other words, the height is equal to (area * 2) / base. The height
            // of this triangle is the distance from the point to the ray.
            return areaOfTriangleTimesTwo / lengthOfBase
        }

        // http://en.wikipedia.org/wiki/Line-plane_intersection
        // This also treats rays as if they were infinite. It will return a
        // point full of NaNs if there is no intersection point.
        fun intersectionPoint(ray: Ray, plane: Geometry.Plane): Point {
            val rayToPlaneVector: Vector = vectorBetween(ray.point, plane.point)

            val scaleFactor = (rayToPlaneVector.dotProduct(plane.normal)
                    / ray.vector.dotProduct(plane.normal))

            return ray.point.translate(ray.vector.scale(scaleFactor))
        }


    }
}


