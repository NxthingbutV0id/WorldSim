package main.graphics

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.stage.FileChooser
import main.utils.Vec2

class InputHandler(private val scene: Scene, private val animator: Animator) {
    private val keyPressed = HashMap<String, BooleanProperty>()
    private val cameraVel = Vec2()

    init {
        setControls()
    }

    private fun setControls() {
        keyPressed["time speed up"] = SimpleBooleanProperty(false)
        keyPressed["time speed down"] = SimpleBooleanProperty(false)
        keyPressed["move up"] = SimpleBooleanProperty(false)
        keyPressed["move left"] = SimpleBooleanProperty(false)
        keyPressed["move down"] = SimpleBooleanProperty(false)
        keyPressed["move right"] = SimpleBooleanProperty(false)
        keyPressed["pause"] = SimpleBooleanProperty(false)
        keyPressed["escape"] = SimpleBooleanProperty(false)
    }

    private fun moveCamera(deltaT: Double) {
        val cameraAcc = Vec2()
        if (keyPressed["move up"]!!.get()) {
            cameraAcc += Vec2(0.0, -1.0)
        } else if (keyPressed["move down"]!!.get()) {
            cameraAcc += Vec2(0.0, 1.0)
        } else {
            cameraVel.y = cameraVel.y * 0.9
        }
        if (keyPressed["move left"]!!.get() && !keyPressed["move right"]!!.get()) {
            cameraAcc += Vec2(-1.0, 0.0)
        } else if (keyPressed["move right"]!!.get() && !keyPressed["move left"]!!.get()) {
            cameraAcc += Vec2(1.0, 0.0)
        } else {
            cameraVel.x = cameraVel.x * 0.9
        }
        cameraAcc.selfNormalize()
        val maxSpeed = 200 * (1 / animator.scale)
        cameraVel += (cameraAcc * (1000 / animator.scale)) * deltaT
        if (cameraVel.magnitude > maxSpeed) {
            cameraVel.selfNormalize()
            cameraVel *= (maxSpeed)
        }
        animator.camera = animator.camera + (cameraVel * deltaT)
    }

    fun handleInput(deltaT: Double) {
        scene.onKeyPressed = EventHandler {event: KeyEvent -> keyPressed(event)}
        scene.onKeyReleased = EventHandler {event: KeyEvent -> keyReleased(event)}
        scene.onMousePressed = EventHandler {event: MouseEvent -> mouseEvent(event)}
        scene.onScroll = EventHandler {event: ScrollEvent -> scrollEvent(event)}
        update(deltaT)
    }

    private fun update(deltaT: Double) {
        var timeScale = animator.timeScale
        if (keyPressed["time speed up"]!!.get()) {
            timeScale *= 1.1
            animator.timeScale = timeScale
        } else if (keyPressed["time speed down"]!!.get()) {
            timeScale *= 0.9
            animator.timeScale = timeScale
        }
        if (!animator.isLockOn) {
            moveCamera(deltaT)
        }
        animator.isPaused = !keyPressed["pause"]!!.get()
    }

    private fun keyPressed(event: KeyEvent) {
        when (event.code) {
            KeyCode.DOWN -> keyPressed["time speed up"]!!.set(true)
            KeyCode.UP -> keyPressed["time speed down"]!!.set(true)
            KeyCode.W -> keyPressed["move up"]!!.set(true)
            KeyCode.A -> keyPressed["move left"]!!.set(true)
            KeyCode.S -> keyPressed["move down"]!!.set(true)
            KeyCode.D -> keyPressed["move right"]!!.set(true)
            KeyCode.SPACE -> keyPressed["pause"]!!.set(!keyPressed["pause"]!!.get())
            KeyCode.ESCAPE -> keyPressed["escape"]!!.set(!keyPressed["escape"]!!.get())
            else -> {}
        }
    }

    private fun keyReleased(event: KeyEvent) {
        when (event.code) {
            KeyCode.DOWN -> keyPressed["time speed up"]!!.set(false)
            KeyCode.UP -> keyPressed["time speed down"]!!.set(false)
            KeyCode.W -> keyPressed["move up"]!!.set(false)
            KeyCode.A -> keyPressed["move left"]!!.set(false)
            KeyCode.S -> keyPressed["move down"]!!.set(false)
            KeyCode.D -> keyPressed["move right"]!!.set(false)
            else -> {}
        }
    }

    private fun pauseMenuClick(event: MouseEvent) {
        if (event.x in 50.0..200.0 && event.y in 250.0..300.0) {
            val fc = FileChooser()
            fc.extensionFilters.add(FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"))
            val file = fc.showOpenDialog(animator.stage)
            if (file != null) {
                animator.setPath(file.path)
            }
        } else if (event.x in 50.0..200.0 && event.y in 350.0..400.0) {
            animator.restart()
        }
    }

    private fun scrollEvent(event: ScrollEvent) {
        var scale = animator.scale
        if (event.deltaY > 1) {
            scale *= 1.1
        } else if (event.deltaY < 1) {
            scale *= 0.9
        }
        animator.scale = scale
    }

    private fun mouseEvent(event: MouseEvent) {
        if (escapePressed()) pauseMenuClick(event) else animator.setTarget(event)
    }

    fun escapePressed(): Boolean {
        return keyPressed["escape"]!!.get()
    }
}