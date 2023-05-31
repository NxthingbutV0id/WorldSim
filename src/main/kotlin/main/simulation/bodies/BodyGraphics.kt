package main.simulation.bodies

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import main.utils.Vec2

class BodyGraphics {
    private var x = 0.0
    private var y = 0.0
    private var relX = 0.0
    private var relY = 0.0
    private var body: CelestialBody? = null
    private var ring: RingSystem? = null

    constructor(body: CelestialBody?) {
        this.body = body
    }

    constructor(ring: RingSystem?) {
        this.ring = ring
    }

    fun drawBody(g: GraphicsContext, scale: Double, screenWidth: Double, screenHeight: Double, relative: Vec2) {
        x = body!!.position.x * scale
        y = body!!.position.y * scale
        val r = body!!.radius * scale
        relX = relative.x * scale
        relY = relative.y * scale
        g.fill = body!!.planetColor
        g.fillOval(
            x - r + screenWidth / 2 - relX,
            y - r + screenHeight / 2 - relY,
            2 * r,
            2 * r
        )
    }

    fun drawBodyText(g: GraphicsContext, scale: Double, screenWidth: Double, screenHeight: Double, relative: Vec2) {
        x = body!!.position.x * scale
        y = body!!.position.y * scale
        relX = relative.x * scale
        relY = relative.y * scale
        g.font = Font("Impact", 20.0)
        g.fill = Color.BLACK
        g.fillText(body!!.name, x + screenWidth / 2 - relX - 2, y + screenHeight / 2 - relY - 2)
        g.fill = Color.WHITE
        g.fillText(body!!.name, x + screenWidth / 2 - relX, y + screenHeight / 2 - relY)
    }

    fun drawRing(g: GraphicsContext, scale: Double, screenWidth: Double, screenHeight: Double, relative: Vec2) {
        x = ring!!.parent.getScreenPosition(scale, screenWidth, screenHeight, relative).x
        y = ring!!.parent.getScreenPosition(scale, screenWidth, screenHeight, relative).y
        val ir: Double = ring!!.innerRadius * scale
        val or: Double = ring!!.outerRadius * scale
        g.fill = ring!!.color
        g.globalAlpha = ring!!.opacity
        g.fillOval(
            x - or,
            y - or,
            2 * or,
            2 * or
        )
        g.globalAlpha = 1.0
        g.fill = Color.BLACK
        g.fillOval(
            x - ir,
            y - ir,
            2 * ir,
            2 * ir
        )
    }
}