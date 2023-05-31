package main.graphics

import javafx.animation.AnimationTimer
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import main.simulation.bodies.CelestialBody
import main.simulation.bodies.Terrestrial
import main.utils.Vec2

class Animator(
    val stage: Stage,
    private val canvas: Canvas,
    scene: Scene,
    var scale: Double,
    var timeScale: Double,
    private val subDivisions: Int
) :
    AnimationTimer() {
    private var lastTime = System.nanoTime()
    private var timer = 0.0
    var isPaused = true
    var camera: Vec2
    var isLockOn = false
        private set
    var target: CelestialBody = Terrestrial("", Double.NaN, Double.NaN, Vec2(0.0, 0.0), Vec2(0.0, 0.0))
        private set
    private var simulationHandler: SimulationHandler
    private val inputHandler: InputHandler
    private val renderer: Renderer
    private var path = "WorldSim/src/main/kotlin/main/files/ExampleSystems/Default.json"
    private var frameRate = 0.0

    init {
        camera = Vec2(0.0, 0.0)
        target.position = camera
        simulationHandler = SimulationHandler(this, path)
        inputHandler = InputHandler(scene, this)
        renderer = Renderer(canvas, this)
    }

    override fun handle(currentTime: Long) {
        val deltaT = (currentTime - lastTime) / 1e9
        lastTime = currentTime
        timer += deltaT
        frameRate = 1 / deltaT
        if (!isPaused) simulationHandler.update(deltaT / timeScale, subDivisions)
        update(deltaT)
        draw()
    }

    private fun update(deltaT: Double) {
        inputHandler.handleInput(deltaT)
        if (timer >= 1.0 / 60.0) {
            for (body in simulationHandler.bodies) {
                body.addToPath()
            }
            timer -= 1.0 / 60.0
        }
    }

    fun setTarget(mouse: MouseEvent) {
        if (!isLockOn) {
            for (body in simulationHandler.bodies) {
                val bodyScreenPos = body.getScreenPosition(scale, canvas.width, canvas.height, camera)
                val bodyScreenRadius = body.radius * scale
                val mousePos = Vec2(mouse.x, mouse.y)
                if (mousePos.distance(bodyScreenPos) <= 2 * bodyScreenRadius) {
                    target = body
                    isLockOn = true
                    break
                }
            }
        } else {
            val mousePos = Vec2(mouse.x, mouse.y)
            val center = Vec2(canvas.width / 2, canvas.height / 2)
            if (mousePos.distance(center) > 100) {
                val newX: Double = target.position.x
                val newY: Double = target.position.y
                target.position = Vec2(newX, newY)
                isLockOn = false
            }
        }
    }

    private fun draw() {
        if (isLockOn) {
            camera = target.position
        }
        renderer.draw(simulationHandler, scale, timeScale, camera, frameRate, simulationHandler.isError)
    }

    fun setPath(path: String) {
        isPaused = true
        this.path = path
        simulationHandler = SimulationHandler(this, path)
    }

    fun restart() {
        simulationHandler = SimulationHandler(this, path)
        isPaused = true
        isLockOn = false
        camera[0.0] = 0.0
    }

    fun getPath(): String {
        return path
    }

    fun escapePressed(): Boolean {
        return inputHandler.escapePressed()
    }
}