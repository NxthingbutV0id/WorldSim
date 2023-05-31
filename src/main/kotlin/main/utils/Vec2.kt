package main.utils

import kotlin.math.*


class Vec2 {
    var x: Double
    var y: Double

    constructor(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    constructor(magnitude: Double, direction: Vec2) {
        x = magnitude * sin(direction.angleRadians)
        y = magnitude * cos(direction.angleRadians)
    }

    constructor() {
        x = 0.0
        y = 0.0
    }

    val magnitude: Double
        get() = sqrt(x * x + y * y)
    val angleRadians: Double
        get() = acos(x / magnitude)

    operator fun set(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    operator fun plus(vec: Vec2): Vec2 {
        return Vec2(x + vec.x, y + vec.y)
    }

    operator fun plusAssign(vec: Vec2) {
        x += vec.x
        y += vec.y
    }

    operator fun minus(vec: Vec2): Vec2 {
        return Vec2(x - vec.x, y - vec.y)
    }

    operator fun times(scalar: Double): Vec2 {
        return Vec2(x * scalar, y * scalar)
    }

    operator fun timesAssign(scalar: Double) {
        x *= scalar
        y *= scalar
    }

    fun distance(vec: Vec2): Double {
        return sqrt(abs(vec.x - x) * abs(vec.x - x) + abs(vec.y - y) * abs(vec.y - y))
    }

    //Pro-tip: taking the dot product of yourself is the same as doing (x^2 + y^2)
    fun dotProduct(vec: Vec2): Double {
        return x * vec.x + y * vec.y
    }

    fun crossProduct(vec: Vec2): Double {
        return x * vec.y - y * vec.x
    }

    fun selfNormalize() {
        val currentMag = magnitude
        if (currentMag != 0.0) {
            x /= currentMag
            y /= currentMag
        }
    }

    fun copy(): Vec2 {
        return Vec2(x, y)
    }

    fun set(other: Vec2) {
        x = other.x
        y = other.y
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}