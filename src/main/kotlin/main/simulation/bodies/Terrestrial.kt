package main.simulation.bodies

import main.utils.Constants
import main.utils.Vec2
import kotlin.math.sqrt

class Terrestrial(name: String, mass: Double, radius: Double, position: Vec2, velocity: Vec2) :
    CelestialBody(name, mass, radius, position, velocity) {
    private var albedo = 0.0 //value from 0 to 1
    private var greenhouseFactor = 0.0 // earth ~= 1.132, venus ~= 3.166, vacuum = 1
    private var hasAtmosphere = false
    fun setGreenhouseFactor(greenhouseFactor: Double) {
        this.greenhouseFactor = greenhouseFactor
    }

    fun setAlbedo(albedo: Double) {
        this.albedo = albedo
    }

    fun setHasAtmosphere(hasAtmosphere: Boolean) {
        this.hasAtmosphere = hasAtmosphere
    }

    override fun setTemp(star: Star?) {
        temperature = when (star) {
            null -> 0.0
            else -> {
                val dist = position.distance(star.position)
                val temp = sqrt(sqrt(star.luminosity * (1 - albedo) / (16 * Math.PI * (dist * dist) * Constants.STEFAN_BOLTZMANN)))
                if (hasAtmosphere) temp * greenhouseFactor else temp
            }
        }
    }
}