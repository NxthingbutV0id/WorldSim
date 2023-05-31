package main.simulation.bodies

import main.utils.Constants
import main.utils.Vec2
import kotlin.math.pow

class Star(name: String, mass: Double, radius: Double, position: Vec2, velocity: Vec2) :
    CelestialBody(name, mass, radius, position, velocity) {
    val luminosity: Double

    init {
        luminosity = setLuminosity() * Constants.L_SOL
        setTemp(this)
    }

    private fun setLuminosity(): Double {
        val relativeMass = mass / Constants.M_SOL
        return if (relativeMass < 0.43) {
            0.23 * relativeMass.pow(2.3) // 0.23 * M^2.3
        } else if (relativeMass < 2) {
            relativeMass * relativeMass * relativeMass * relativeMass // M^4
        } else {
            1.4 * (relativeMass * relativeMass * relativeMass * Math.sqrt(relativeMass)) // 1.4 * M^3.5
        }
    }

    override fun setTemp(star: Star?) {
        val relativeRadius = radius / Constants.R_SOL
        temperature = (setLuminosity() / (relativeRadius * relativeRadius)).pow(0.25) * Constants.T_SOL
    }
}