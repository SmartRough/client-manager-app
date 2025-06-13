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
	public void initialize() {
		// Al iniciar el controlador, carga el listado de compañías
		loadCompanyList();
	}

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

	private void loadCompanyList() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/smartrough/app/view/CompanyListView.fxml"));
			Parent view = loader.load();
			root.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
