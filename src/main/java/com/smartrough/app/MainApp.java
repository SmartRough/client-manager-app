package com.smartrough.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		// Crea la base de datos y tablas si no existen
		com.smartrough.app.dao.Database.initializeSchema();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/smartrough/app/MainView.fxml"));
		Scene scene = new Scene(loader.load(), 800, 600);
		stage.setTitle("Client Manager App");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
