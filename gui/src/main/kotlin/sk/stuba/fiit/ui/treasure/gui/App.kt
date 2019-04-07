package sk.stuba.fiit.ui.treasure.gui

import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextInputDialog
import javafx.scene.layout.VBox
import javafx.stage.Stage

class App : Application() {
    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(this::class.java.getResource("/main_screen.fxml"))
        val parent = loader.load<Parent>()

        val scene = Scene(parent, 1280.0, 800.0)
        primaryStage.minWidth = 640.0
        primaryStage.minHeight = 400.0
        primaryStage.scene = scene

        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    launch(App::class.java, *args)
}
