package main.simulation.bodies

import javafx.scene.canvas.GraphicsContext
import main.utils.Constants
import main.utils.Vec2
import java.util.*
import kotlin.math.*

class OrbitGraphics(private val body: CelestialBody) {
    private val path = LinkedList<Vec2>()
    private var rMax = 0.0
    private var a = 0.0
    private var r = 0.0
    private var mu = 0.0
    private var v = 0.0
    private var relativeVel: Vec2? = null

    init {
        path.add(Vec2(body.position.x, body.position.y))
    }

    fun addToPath() {
        val currentPos = Vec2(body.position.x, body.position.y)
        path.add(currentPos)
        if (path.size > 1000) {
            path.removeAt(0)
        }
    }

    fun drawOrbit(gc: GraphicsContext, scale: Double, screenWidth: Double, screenHeight: Double, camera: Vec2) {
        if (body.parent != null) {
            setValues()
            drawBodyConic(gc, scale, screenWidth, screenHeight, camera)
        } else {
            drawBodyPath(gc, scale, screenWidth, screenHeight, camera)
        }
    }

    private fun drawBodyConic(
        gc: GraphicsContext,
        scale: Double,
        screenWidth: Double,
        screenHeight: Double,
        camera: Vec2
    ) {
        val res = 1000
        val parentScreenPos = body.parent!!.getScreenPosition(scale, screenWidth, screenHeight, camera)
        val relativePos: Vec2 = body.position - body.parent!!.position
        val vTheta = relativePos.crossProduct(relativeVel!!) / r
        val e = sqrt(1 + r * vTheta * vTheta / mu * (r * v * v / mu - 2))
        val vRadial = relativePos.dotProduct(relativeVel!!) / r
        val deltaTheta = sign(vTheta * vRadial) * acos((a*(1 - e*e)-r)/(e*r))-atan2(relativePos.y, relativePos.x)
        gc.stroke = body.planetColor
        gc.lineWidth = 4.0
        gc.beginPath()
        val thetaMax = acos((a * (1 - e * e) - rMax) / (e * rMax))
        if (e >= 1) {
            var theta = -thetaMax
            while (theta <= thetaMax) {
                drawConic(gc, scale, parentScreenPos, a, e, deltaTheta, theta)
                theta += 2 * Math.PI / res
            }
        } else {
            var theta = 0.0
            while (theta <= 2 * Math.PI) {
                drawConic(gc, scale, parentScreenPos, a, e, deltaTheta, theta)
                theta += 2 * Math.PI / res
            }
        }
        gc.stroke()
    }

    private fun setValues() {
        mu = Constants.GRAVITATIONAL_CONSTANT * body.parent!!.mass
        relativeVel = body.velocity - body.parent!!.velocity
        r = body.position.distance(body.parent!!.position)
        v = relativeVel!!.magnitude
        a = mu * r / (2 * mu - r * v * v)
        rMax = if (body.parent!!.parent != null) {
            a * (body.parent!!.mass / body.parent!!.parent!!.mass).pow(2.5)
        } else {
            1e10 * body.parent!!.radius
        }
    }

    private fun drawConic(
        gc: GraphicsContext, scale: Double, parentScreenPos: Vec2,
        a: Double, e: Double, deltaTheta: Double, theta: Double
    ) {
        val pathX = r(theta, a, e) * cos(theta + deltaTheta)
        val pathY = -r(theta, a, e) * sin(theta + deltaTheta)
        if (theta == 0.0) {
            gc.moveTo(pathX * scale + parentScreenPos.x, pathY * scale + parentScreenPos.y)
        } else {
            gc.lineTo(pathX * scale + parentScreenPos.x, pathY * scale + parentScreenPos.y)
        }
    }

    private fun r(theta: Double, a: Double, e: Double): Double {
        return a * (1 - e * e) / (1 + e * cos(theta))
    }

    private fun sign(x: Double): Double {
        return x / abs(x)
    }

    private fun drawBodyPath(
        gc: GraphicsContext,
        scale: Double,
        screenWidth: Double,
        screenHeight: Double,
        relative: Vec2
    ) {
        val offsetX = screenWidth / 2
        val offsetY = screenHeight / 2
        gc.stroke = body.planetColor
        var thickness = 4.0
        for (i in path.size - 1 downTo 2) {
            gc.lineWidth = thickness
            val pos1 = path[i - 1]
            val pos2 = path[i]
            val points = computeLinePoints(pos1, pos2, offsetX, offsetY, scale, relative)
            if (shouldSkipDrawing(Vec2(points[0], points[1]), Vec2(points[2], points[3]), screenWidth, screenHeight)) {
                continue
            }
            gc.strokeLine(points[0], points[1], points[2], points[3])
            thickness *= 0.99
        }
    }

    private fun shouldSkipDrawing(pos1: Vec2, pos2: Vec2, screenWidth: Double, screenHeight: Double): Boolean {
        val bothSamePoint = pos1.x == pos2.x && pos1.y == pos2.y
        val isOnScreen = pos1.x in 0.0..screenWidth && pos1.y in 0.0..screenHeight
        return bothSamePoint || !isOnScreen
    }

    private fun computeLinePoints(
        pos1: Vec2, pos2: Vec2, offsetX: Double, offsetY: Double, scale: Double, relative: Vec2
    ): DoubleArray {
        val relX = relative.x
        val relY = relative.y
        val xPrev = (pos1.x + offsetX / scale - relX) * scale
        val yPrev = (pos1.y + offsetY / scale - relY) * scale
        val xCurr = (pos2.x + offsetX / scale - relX) * scale
        val yCurr = (pos2.y + offsetY / scale - relY) * scale
        return doubleArrayOf(xPrev, yPrev, xCurr, yCurr)
    }
}