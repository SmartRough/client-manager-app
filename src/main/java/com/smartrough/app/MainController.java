package com.smartrough.app;

import com.smartrough.app.util.ViewNavigator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainController {

	@FXML
	private BorderPane root;

	@FXML
	private Button btnClientes;

	@FXML
	public void initialize() {
		ViewNavigator.setMainLayout(root);
		ViewNavigator.loadView("CompanyListView.fxml");
	}
	
	@FXML
	private void showOwnCompanyForm() {
		ViewNavigator.loadView("OwnCompanyFormView.fxml");
	}

	@FXML
	private void showCompanyForm() {
		ViewNavigator.loadView("CompanyFormView.fxml");
	}

	@FXML
	private void showCompanyList() {
		ViewNavigator.loadView("CompanyListView.fxml");
	}

	@FXML
	private void showContracts() {
		ViewNavigator.loadView("ContractListView.fxml");
	}

	@FXML
	private void showInvoices() {
		ViewNavigator.loadView("InvoiceFormView.fxml");
	}

	@FXML
	private void showEstimates() {
		ViewNavigator.loadView("EstimateListView.fxml");
	}
}
