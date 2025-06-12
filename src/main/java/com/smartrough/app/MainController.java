package com.smartrough.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainController {

	@FXML
	private BorderPane root;

	@FXML
	private Button btnClientes;

	@FXML
	private void showCompanyForm() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/smartrough/app/form/CompanyForm.fxml"));
			Parent view = loader.load();
			root.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
