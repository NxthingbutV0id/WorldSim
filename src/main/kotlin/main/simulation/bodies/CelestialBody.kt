package main.simulation.bodies

import javafx.scene.paint.Paint
import main.utils.Vec2

abstract class CelestialBody(
    var name: String,
    var mass: Double,
    var radius: Double,
    var position: Vec2,
    var velocity: Vec2
) {
    var planetColor: Paint? = null
    var temperature = 0.0
        protected set
    var parent: CelestialBody? = null
    val bodyGraphics: BodyGraphics
    val orbitGraphics: OrbitGraphics

    init {
        bodyGraphics = BodyGraphics(this)
        orbitGraphics = OrbitGraphics(this)
    }

    fun addToPath() {
        orbitGraphics.addToPath()
    }

    fun getScreenPosition(scale: Double, screenWidth: Double, screenHeight: Double, relative: Vec2): Vec2 {
        val x: Double = position.x * scale
        val y: Double = position.y * scale
        val relX: Double = relative.x * scale
        val relY: Double = relative.y * scale
        return Vec2(x + screenWidth / 2 - relX, y + screenHeight / 2 - relY)
    }

    abstract fun setTemp(star: Star?)
}