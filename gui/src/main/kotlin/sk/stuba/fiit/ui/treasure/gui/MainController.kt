package sk.stuba.fiit.ui.treasure.gui

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.Initializable
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.util.converter.NumberStringConverter
import kotlinx.coroutines.*
import sk.stuba.fiit.ui.treasure.evolution.selection.SelectionMethod
import sk.stuba.fiit.ui.treasure.evolution.selection.TournamentSelection
import java.net.URL
import java.util.*

enum class CrossoverMethod {
    TOURNAMENT, ROULETTE
}

data class CrossoverSelection(val selection: CrossoverMethod, val name: String) {
    override fun toString() = name
}

class MainController : Initializable, CoroutineScope by MainScope() {
    lateinit var historyPane: AnchorPane
    lateinit var mainPane: ScrollPane
    lateinit var detailsPane: AnchorPane
    lateinit var runButton: Button
    lateinit var stopButton: Button
    lateinit var fitnessChart: LineChart<Int, Int>
    lateinit var graphHelper: FitnessGraphHelper

    lateinit var historyTable: TableView<History>
    lateinit var generationColumn: TableColumn<History, Int>
    lateinit var fitnessColumn: TableColumn<History, Int>

    lateinit var populationSizeTextField: TextField
    lateinit var chromosomeSizeTextField: TextField
    lateinit var eliteCheckbox: CheckBox
    lateinit var eliteClonesTextField: TextField
    lateinit var mutationSlider: Slider
    lateinit var mutationTextField: TextField
    lateinit var crossoverComboBox: ComboBox<CrossoverSelection>
    lateinit var variableMutationCheckBox: CheckBox
    lateinit var solutionCheckBox: CheckBox

    lateinit var generationLabel: Label
    lateinit var fitnessLabel: Label
    lateinit var mutationVarianceLabel: Label
    lateinit var outputLabel: Label

    private val evolutionHelper = EvolutionHelper(this)
    private val historyData = FXCollections.observableArrayList<History>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        graphHelper = FitnessGraphHelper(fitnessChart)
        historyTable.items = historyData
        historyTable.sortOrder.add(fitnessColumn)

        mutationSlider.valueProperty().addListener { _, _, value -> mutationSlider.value = value.toInt().toDouble() }
        mutationTextField.textProperty().bindBidirectional(mutationSlider.valueProperty(), NumberStringConverter())
        eliteClonesTextField.disableProperty().bind(eliteCheckbox.selectedProperty().not())

        val crossoverItems = FXCollections.observableArrayList<CrossoverSelection>(
                CrossoverSelection(CrossoverMethod.TOURNAMENT, "Tournament"),
                CrossoverSelection(CrossoverMethod.ROULETTE, "Roulette")
        )
        crossoverComboBox.items = crossoverItems
        crossoverComboBox.selectionModel.selectFirst()

        Platform.runLater { runButton.requestFocus() }
    }

    fun onRunButtonClick(event: ActionEvent) {
        clear()
        setDisableControls(true)
        launch(Dispatchers.Default) {
            val populationSize = populationSizeTextField.text.toInt()
            val chromosomeSize = chromosomeSizeTextField.text.toInt()
            val mutationChance = (mutationSlider.value / 100.0).toFloat()
            val eliteClones = if (eliteCheckbox.isSelected) eliteClonesTextField.text.toInt() else 0
            val crossoverMethod = crossoverComboBox.value.selection
            val variableMutation = variableMutationCheckBox.isSelected
            val endAfterSolution = solutionCheckBox.isSelected
            val result = evolutionHelper.run(
                    populationSize,
                    chromosomeSize,
                    mutationChance,
                    eliteClones,
                    crossoverMethod,
                    variableMutation,
                    endAfterSolution,
                    { best, generation, path ->
                val pathString = path.joinToString()
                historyData.add(History(generation, best, pathString))
                historyTable.sort()

                generationLabel.text = generation.toString()
                fitnessLabel.text = best.toString()
                outputLabel.text = pathString
            }, {
                graphHelper.bestSeries.data.add(XYChart.Data(it.generation, it.fitnessBest))
                graphHelper.averageSeries.data.add(XYChart.Data(it.generation, it.fitnessAverage))
                graphHelper.worstSeries.data.add(XYChart.Data(it.generation, it.fitnessWorst))
            }, {
                mutationVarianceLabel.text = "${(it * 100).toInt()} %"
            }).await()

            withContext(Dispatchers.Main) {
                runButton.isDisable = false
                stopButton.isDisable = true

                generationLabel.text = result.generation.toString()
                fitnessLabel.text = result.fitnessBest.toString()
                outputLabel.text = historyData[0].path

                setDisableControls(false)
            }
        }
        runButton.isDisable = true
        stopButton.isDisable = false
    }

    fun onStopButtonClick(event: ActionEvent) {
        evolutionHelper.stop()
    }

    fun onClearButtonClick(event: ActionEvent) {
        clear()
    }

    private fun setDisableControls(disable: Boolean) {
        if (disable) {
            eliteClonesTextField.disableProperty().unbind()
        }
        populationSizeTextField.isDisable = disable
        chromosomeSizeTextField.isDisable = disable
        eliteCheckbox.isDisable = disable
        eliteClonesTextField.isDisable = disable
        mutationSlider.isDisable = disable
        mutationTextField.isDisable = disable
        crossoverComboBox.isDisable = disable
        variableMutationCheckBox.isDisable = disable
        if (!disable) {
            eliteClonesTextField.disableProperty().bind(eliteCheckbox.selectedProperty().not())
        }
    }

    private fun clear() {
        graphHelper.reset()
        historyData.clear()
        generationLabel.text = "0"
        fitnessLabel.text = "0"
        mutationVarianceLabel.text = "0 %"
        outputLabel.text = ""
    }
}