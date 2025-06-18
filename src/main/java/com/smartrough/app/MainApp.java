package com.smartrough.app;

import com.smartrough.app.dao.Database;
import com.smartrough.app.util.ViewNavigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// Crea la base de datos y tablas si no existen
		Database.initializeSchema();

		// Carga MainView como layout base
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/smartrough/app/MainView.fxml"));
		BorderPane rootLayout = loader.load();

		// Establece la referencia del contenedor principal en ViewNavigator
		ViewNavigator.setMainLayout(rootLayout);

		// Carga por defecto el listado de compañías
		ViewNavigator.loadView("CompanyListView.fxml");

		// Configura y muestra
		stage.setScene(new Scene(rootLayout, 800, 600));
		stage.setTitle("Client Manager App");
		stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/img/robot.png")));
		stage.setResizable(true);
		stage.setMaximized(true);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
