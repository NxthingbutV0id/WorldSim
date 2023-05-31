package main.simulation

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import main.graphics.Animator

class SimulationManager : Application() {
    private var width = 1920.0
    private var height = 1080.0
    private lateinit var animator: Animator
    private lateinit var stage: Stage

    @Throws(Exception::class)
    override fun start(stage: Stage) {
        this.stage = stage
        initialize()
    }

    private fun initialize() {
        val timeScale = 1.0
        val scale = 1.0
        val subDivisions = 10000

        val root = BorderPane()
        val mainScene = Scene(root)
        val canvas = Canvas(width, height)

        root.children.add(canvas)
        animator = Animator(stage, canvas, mainScene, scale, timeScale, subDivisions)

        setStage(mainScene)
    }

    private fun setStage(mainScene: Scene) {
        val version = "1.0.1"
        stage.title = "WorldSim V$version"

        stage.height = height
        stage.width = width
        stage.isResizable = false
        stage.scene = mainScene

        animator.start()
        stage.show()
    }
}