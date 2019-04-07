package sk.stuba.fiit.ui.treasure.gui

import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart

class FitnessGraphHelper(val fitnessChart: LineChart<Int, Int>) {
    val bestSeries = XYChart.Series<Int, Int>()
    val averageSeries = XYChart.Series<Int, Int>()
    val worstSeries = XYChart.Series<Int, Int>()

    init {
        fitnessChart.xAxis.label = "No of generation"
        fitnessChart.yAxis.label = "Fitness"

        bestSeries.name = "Best fitness"
        averageSeries.name = "Average fitness"
        worstSeries.name = "Worst fitness"

        fitnessChart.data.add(bestSeries)
        fitnessChart.data.add(averageSeries)
        fitnessChart.data.add(worstSeries)
    }

    fun reset() {
        bestSeries.data.clear()
        averageSeries.data.clear()
        worstSeries.data.clear()
    }
}