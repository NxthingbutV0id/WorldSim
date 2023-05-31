package main.simulation.bodies

import javafx.scene.paint.Paint
import main.utils.Constants
import main.utils.Vec2
import kotlin.math.sqrt

class GasGiant(name: String, mass: Double, radius: Double, position: Vec2, velocity: Vec2) :
    CelestialBody(name, mass, radius, position, velocity) {
    private var albedo = 0.0
    var rings: RingSystem? = null
        private set

    fun setAlbedo(albedo: Double) {
        this.albedo = albedo
    }

    fun setRings(innerRadius: Double, outerRadius: Double, color: Paint?, opacity: Double) {
        rings = RingSystem(this, innerRadius, outerRadius, color!!, opacity)
    }

    override fun setTemp(star: Star?) {
        temperature = if (star != null) {
            val dist = position.distance(star.position)
            sqrt(
                sqrt(
                    star.luminosity * (1 - albedo) / (16 * Math.PI * (dist * dist) * Constants.STEFAN_BOLTZMANN)
                )
            )
        } else {
            0.0
        }
    }

    fun hasRings(): Boolean {
        return rings != null
    }
}