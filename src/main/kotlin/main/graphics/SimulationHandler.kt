package main.graphics

import main.simulation.SimulationSolver
import main.simulation.bodies.CelestialBody

class SimulationHandler(animator: Animator?, path: String) {
    private val simulator: SimulationSolver

    init {
        simulator = SimulationSolver(animator!!)
        simulator.createBodies(path)
    }

    fun update(deltaT: Double, subDivisions: Int) {
        simulator.update(deltaT, subDivisions)
    }

    val bodies: List<CelestialBody>
        get() = simulator.getBodies()
    val isError: Boolean
        get() = simulator.isError
}