package main.files

import org.slf4j.LoggerFactory
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import main.simulation.bodies.CelestialBody
import main.utils.Constants
import org.slf4j.Logger
import java.util.*

class Decoder {
    private val logger: Logger = LoggerFactory.getLogger(Decoder::class.java)
    fun getMass(str: String): Double {
        var massValue = extractDouble(str)
        if (str.contains("Ms")) {
            massValue *= Constants.M_SOL
        } else if (str.contains("Me")) {
            massValue *= Constants.M_EARTH
        } else if (str.contains("Mj")) {
            massValue *= Constants.M_JUPITER
        } else if (str.contains("Mm")) {
            massValue *= Constants.M_MOON
        }
        return massValue
    }

    fun getRadius(str: String): Double {
        var radiusValue = extractDouble(str)
        if (str.contains("Rs")) {
            radiusValue *= Constants.R_SOL
        } else if (str.contains("Re")) {
            radiusValue *= Constants.R_EARTH
        } else if (str.contains("Rj")) {
            radiusValue *= Constants.R_JUPITER
        } else if (str.contains("Rm")) {
            radiusValue *= Constants.R_MOON
        }
        return radiusValue
    }

    fun getDistance(str: String): Double {
        var dist = extractDouble(str)
        if (str.contains("Au")) {
            dist *= Constants.ASTRONOMICAL_UNIT
        } else if (str.contains("Km")) {
            dist *= Constants.KILOMETER
        }
        return dist
    }

    fun getColor(str: String): Paint {
        return if (str[0] == '#') {
            Paint.valueOf(str)
        } else {
            logger.warn("Error loading planet color, defaulting to white")
            Color.WHITE
        }
    }

    private fun extractDouble(str: String): Double {
        return try {
            str.replace("[^\\d.Ee+-]".toRegex(), "").toDouble()
        } catch (e: Exception) {
            str.replace("[^\\d.]".toRegex(), "").toDouble()
        }
    }

    fun setRelative(str: String, bodies: LinkedList<CelestialBody>, body: CelestialBody) {
        for (otherBody in bodies) {
            if (otherBody.name == str) {
                body.parent = otherBody
                body.position = body.position + otherBody.position
                body.velocity = body.velocity + otherBody.velocity
                return
            }
        }
    }
}