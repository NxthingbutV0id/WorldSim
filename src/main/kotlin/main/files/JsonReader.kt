package main.files

import org.slf4j.LoggerFactory
import main.simulation.bodies.CelestialBody
import main.simulation.bodies.GasGiant
import main.simulation.bodies.Star
import main.simulation.bodies.Terrestrial
import main.utils.Vec2
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import org.slf4j.Logger
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.util.*

class JsonReader {
    private val decoder = Decoder()
    private var rawData: JSONObject? = null
    private var settings: JSONObject? = null
    private val bodies = LinkedList<CelestialBody>()
    private val logger: Logger = LoggerFactory.getLogger(JsonReader::class.java)
    fun loadFile(path: String): LinkedList<CelestialBody> {
        try {
            parseJSON(FileReader(path))
        } catch (e: FileNotFoundException) {
            logger.error("Error: Default file not found! Make sure everything is installed properly!")
        } catch (e: Exception) {
            logger.error("Error loading file -> $e")
        }
        return bodies
    }

    @Throws(IOException::class, ParseException::class, InvalidFileFormatException::class)
    fun parseJSON(reader: FileReader?) {
        val parser = JSONParser()
        val jsonObj = parser.parse(reader)
        rawData = jsonObj as JSONObject
        getSettings()
        loadBodies()
    }

    @Throws(InvalidFileFormatException::class)
    fun loadBodies() {
        val array: JSONArray = rawData?.get("system") as JSONArray
        for (o in array) {
            val body: JSONObject = o as JSONObject
            setupBodies(body)
        }
    }

    @Throws(InvalidFileFormatException::class)
    fun setupBodies(body: JSONObject) {
        val bodyType = body["type"] as String
        val name = body["name"] as String
        val mass = decoder.getMass(body["mass"].toString())
        val radius = decoder.getRadius(body["radius"].toString())
        val position = arrayToVec(body["position"] as JSONArray)
        val velocity = arrayToVec(body["velocity"] as JSONArray)
        when (bodyType) {
            "Star" -> createStar(body, name, mass, radius, position, velocity)
            "Terrestrial" -> createTerrestrial(body, name, mass, radius, position, velocity)
            "Gas Giant" -> createGasGiant(body, name, mass, radius, position, velocity)
            else -> throw InvalidFileFormatException("Error, Type not found")
        }
        logger.info("Body \"{}\" successfully loaded!", name)
    }

    private fun createStar(body: JSONObject, name: String, mass: Double, radius: Double, position: Vec2, velocity: Vec2) {
        val temp = Star(name, mass, radius, position, velocity)
        temp.planetColor = decoder.getColor(body["color"].toString())
        if (body["parent"] != null) {
            decoder.setRelative((body["parent"] as String), bodies, temp)
        }
        bodies.add(temp)
    }

    private fun createTerrestrial(
        body: JSONObject, name: String, mass: Double,
        radius: Double, position: Vec2, velocity: Vec2
    ) {
        val temp = Terrestrial(name, mass, radius, position, velocity)
        temp.planetColor = decoder.getColor(body["color"].toString())
        if (body["albedo"] is Number) {
            temp.setAlbedo((body["albedo"] as Number).toDouble())
        }
        temp.setHasAtmosphere(body["atmosphere present?"] as Boolean)
        if (body["greenhouse effect"] is Number) {
            temp.setGreenhouseFactor((body["greenhouse effect"] as Number).toDouble())
        }
        if (body["parent"] != null) {
            decoder.setRelative((body["parent"] as String), bodies, temp)
        }
        bodies.add(temp)
    }

    private fun createGasGiant(
        body: JSONObject,
        name: String,
        mass: Double,
        radius: Double,
        position: Vec2,
        velocity: Vec2
    ) {
        val temp = GasGiant(name, mass, radius, position, velocity)
        temp.planetColor = decoder.getColor(body["color"].toString())
        temp.setAlbedo(body["albedo"] as Double)
        setupRing(body, temp)
        if (body["parent"] != null) {
            decoder.setRelative((body["parent"] as String), bodies, temp)
        }
        bodies.add(temp)
    }

    private fun setupRing(body: JSONObject, gg: GasGiant) {
        val ring: JSONObject? = body["ring system"] as JSONObject?
        if (ring != null) {
            val ir = ring["inner radius"] as Double
            val or = ring["outer radius"] as Double
            val col = decoder.getColor(ring["color"].toString())
            val op = (ring["opacity"] as String).toDouble()
            gg.setRings(ir, or, col, op)
        }
    }

    private fun arrayToVec(arr: JSONArray): Vec2 {
        val x: Double = try {
            arr[0].toString().toDouble()
        } catch (e: Exception) {
            decoder.getDistance(arr[0].toString())
        }
        val y: Double = try {
            arr[1].toString().toDouble()
        } catch (e: Exception) {
            decoder.getDistance(arr[1].toString())
        }
        return Vec2(x, -y)
    }

    private fun getSettings() {
        settings = rawData?.get("settings") as JSONObject
    }

    val scale: Double
        get() = settings?.get("zoom").toString().toDouble()
    val timeScale: Double
        get() = 1 / settings?.get("time scale").toString().toDouble()
}