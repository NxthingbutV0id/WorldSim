package main.simulation.bodies

import javafx.scene.paint.Paint

class RingSystem(
    val parent: CelestialBody,
    innerRadius: Double,
    outerRadius: Double,
    val color: Paint,
    val opacity: Double
) {
    val innerRadius: Double
    val outerRadius: Double
    val graphics: BodyGraphics

    init {
        this.innerRadius = innerRadius * parent.radius
        this.outerRadius = outerRadius * parent.radius
        graphics = BodyGraphics(this)
    }
}