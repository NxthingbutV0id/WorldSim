package main.graphics

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import main.simulation.bodies.GasGiant
import main.utils.Vec2
import java.nio.file.Path
import kotlin.math.roundToInt

class Renderer(private val canvas: Canvas, private val animator: Animator) {
    private var gc: GraphicsContext? = null
    private val screenWidth: Double = canvas.width
    private val screenHeight: Double = canvas.height

    fun draw(
        simulationHandler: SimulationHandler, scale: Double, timeScale: Double, camera: Vec2, fps: Double,
        error: Boolean
    ) {
        gc = canvas.graphicsContext2D
        drawBodies(simulationHandler, scale, camera)
        drawBodyText(simulationHandler, scale, camera)
        if (animator.escapePressed()) {
            drawPauseMenu()
        } else {
            if (!error) {
                drawText(scale, timeScale, fps)
            }
        }
        if (error) {
            errorMsg()
        }
    }

    private fun drawBodies(simulationHandler: SimulationHandler, scale: Double, camera: Vec2) {
        gc!!.fill = Color.BLACK
        gc!!.fillRect(0.0, 0.0, canvas.width, canvas.height)
        for (body in simulationHandler.bodies) {
            if (body is GasGiant && body.hasRings()) {
                body.rings!!.graphics.drawRing(gc!!, scale, canvas.width, canvas.height, camera)
            }
            body.orbitGraphics.drawOrbit(gc!!, scale, canvas.width, canvas.height, camera)
            body.bodyGraphics.drawBody(gc!!, scale, canvas.width, canvas.height, camera)
        }
    }

    private fun drawText(scale: Double, timeScale: Double, fps: Double) {
        var initTextPos = 100.0
        val target = animator.target
        gc!!.fill = Color.WHITE
        val pathFromString = Path.of(animator.getPath())
        val file = pathFromString.fileName.toString()
        gc!!.fillText("Scenario: $file", 50.0, 50.0)
        gc!!.fillText("FPS: " + roundTwo(fps), screenWidth - 200, 50.0)
        gc!!.fillText("Zoom level: $scale", 50.0, initTextPos)
        initTextPos += 20.0
        gc!!.fillText("Time scale: ${1 / timeScale}x real time", 50.0, initTextPos)
        initTextPos += 20.0
        if (animator.isLockOn) {
            gc!!.fillText("Current target: ${target.name}", 50.0, initTextPos)
            initTextPos += 20.0
            gc!!.fillText("Mass: ${target.mass} kg", 50.0, initTextPos)
            initTextPos += 20.0
            gc!!.fillText("Radius: ${target.radius} m", 50.0, initTextPos)
            initTextPos += 20.0
            gc!!.fillText("Temperature: ${roundTwo(target.temperature)} K", 50.0, initTextPos)
            initTextPos += 20.0
            gc!!.fillText(
                "Temperature: ${roundTwo(target.temperature - 273.15)} C", 50.0, initTextPos
            )
        } else {
            gc!!.fillText("Current target: None", 50.0, initTextPos)
        }
    }

    private fun drawBodyText(simulationHandler: SimulationHandler, scale: Double, camera: Vec2) {
        for (body in simulationHandler.bodies) {
            body.bodyGraphics.drawBodyText(gc!!, scale, canvas.width, canvas.height, camera)
        }
    }

    private fun drawPauseMenu() {
        gc!!.fill = Color.GRAY
        gc!!.fillRect(0.0, 0.0, canvas.width / 4, canvas.height)
        gc!!.fill = Color.BLUE
        gc!!.fillRect(50.0, 250.0, 150.0, 50.0)
        gc!!.fill = Color.WHITE
        gc!!.fillText("Load File", 80.0, 280.0)
        gc!!.fill = Color.BLUE
        gc!!.fillRect(50.0, 350.0, 150.0, 50.0)
        gc!!.fill = Color.WHITE
        gc!!.fillText("Reload File", 75.0, 380.0)
    }

    private fun roundTwo(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }

    private fun errorMsg() {
        gc!!.fill = Color.WHITE
        val pathFromString = Path.of(animator.getPath())
        val file = pathFromString.fileName.toString()
        gc!!.fillText(
            "ERROR, $file is not in the correct format, please select a valid file.",
            50.0, 50.0
        )
    }
}