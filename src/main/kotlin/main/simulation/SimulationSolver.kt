package main.simulation

import org.slf4j.LoggerFactory
import main.files.JsonReader
import main.graphics.Animator
import main.simulation.bodies.*
import main.utils.*
import org.slf4j.Logger
import java.util.*
import kotlin.math.sqrt

class SimulationSolver(animator: Animator) {
    private var bodies: LinkedList<CelestialBody> = LinkedList<CelestialBody>()
    private val animator: Animator
    private val logger: Logger = LoggerFactory.getLogger(SimulationSolver::class.java)
    private val stars: LinkedList<Star> = LinkedList<Star>()
    var isError = false
        private set

    init {
        this.animator = animator
    }

    fun createBodies(path: String) {
        try {
            val reader = JsonReader()
            bodies = reader.loadFile(path)
            animator.scale = reader.scale
            animator.timeScale = reader.timeScale
        } catch (ignore: Exception) {
            isError = true
        }
        for (body in bodies) {
            if (body is Star) {
                stars.add(body)
            }
        }
    }

    fun update(deltaT: Double, timeStep: Int) {
        val dt: Double = deltaT / timeStep
        for (i in 0 until timeStep) {
            for (body in bodies) {
                //Runge-Kutta 4 method
                val originalVelocity: Vec2 = body.velocity.copy()
                val originalPosition: Vec2 = body.position.copy()
                val k1v: Vec2 = getAcceleration(body) * dt
                val k1x: Vec2 = originalVelocity * dt
                body.velocity = body.velocity + (k1v * 0.5)
                body.position = body.position + (k1x * 0.5)
                val k2v: Vec2 = getAcceleration(body) * dt
                val k2x: Vec2 = body.velocity * dt
                body.velocity = originalVelocity + (k2v * (0.5))
                body.position = originalPosition + (k2x * (0.5))
                val k3v: Vec2 = getAcceleration(body) * dt
                val k3x: Vec2 = body.velocity * dt
                body.velocity = originalVelocity + k3v
                body.position = originalPosition + k3x
                val k4v: Vec2 = getAcceleration(body) * dt
                val k4x: Vec2 = body.velocity * dt
                body.velocity = originalVelocity
                body.position = originalPosition
                val deltaV: Vec2 = k1v + (k2v * 2.0) + (k3v * 2.0) + (k4v * (1.0 / 6.0))
                val deltaX: Vec2 = k1x + (k2x * 2.0) + (k3x * 2.0) + (k4x * (1.0 / 6.0))
                body.velocity = body.velocity + deltaV
                body.position = body.position + deltaX
                for (star in stars) {
                    body.setTemp(star)
                }
            }
        }
    }

    private fun getAcceleration(body: CelestialBody): Vec2 {
        var dx: Double
        var dy: Double
        var r: Double
        var f: Double
        var ax = 0.0
        var ay = 0.0
        for (otherBody in bodies) {
            if (otherBody !== body) {
                dx = otherBody.position.x - body.position.x
                dy = otherBody.position.y - body.position.y
                r = sqrt(dx * dx + dy * dy)
                f = Constants.GRAVITATIONAL_CONSTANT * otherBody.mass / (r * r)
                ax += f * dx / r
                ay += f * dy / r
            }
        }
        if (java.lang.Double.isNaN(ax) || java.lang.Double.isNaN(ay)) {
            logger.error(
                "Possible collision detected at {}, {} acceleration is NaN", body.position, body.name
            )
        }
        return Vec2(ax, ay)
    }

    fun getBodies(): LinkedList<CelestialBody> {
        return bodies
    }
}