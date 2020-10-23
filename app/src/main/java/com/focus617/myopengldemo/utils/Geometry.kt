package com.focus617.myopengldemo.utils


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

}


