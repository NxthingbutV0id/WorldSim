package main

import main.simulation.SimulationManager
import javafx.application.Application

fun main(args: Array<String>) {
    Application.launch(SimulationManager::class.java, *args)
}