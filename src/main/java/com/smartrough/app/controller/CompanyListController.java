package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CompanyListController {

	@FXML
	private TableView<Company> companyTable;
	@FXML
	private TableColumn<Company, String> nameCol;
	@FXML
	private TableColumn<Company, String> repCol;
	@FXML
	private TableColumn<Company, String> phoneCol;
	@FXML
	private TableColumn<Company, String> emailCol;
	@FXML
	private TableColumn<Company, Boolean> ownCol;
	@FXML
	private TableColumn<Company, Void> actionCol;

	private final ObservableList<Company> companies = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		repCol.setCellValueFactory(new PropertyValueFactory<>("representative"));
		phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		ownCol.setCellValueFactory(new PropertyValueFactory<>("ownCompany"));

		// Agrega botones con íconos
		actionCol.setCellFactory(col -> new TableCell<>() {
			private final Button editBtn = new Button();
			private final Button deleteBtn = new Button();
			private final HBox box = new HBox(5, editBtn, deleteBtn);

			{
				// Icono de editar
				ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/edit.png")));
				editIcon.setFitHeight(16);
				editIcon.setFitWidth(16);
				editBtn.setGraphic(editIcon);
				editBtn.getStyleClass().add("icon-button");
				editBtn.setTooltip(new Tooltip("Edit"));

				// Icono de eliminar
				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				deleteIcon.setFitHeight(16);
				deleteIcon.setFitWidth(16);
				deleteBtn.setGraphic(deleteIcon);
				deleteBtn.getStyleClass().add("icon-button");
				deleteBtn.setTooltip(new Tooltip("Delete"));

				// Acciones
				editBtn.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
				deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));

				box.setStyle("-fx-alignment: center;");
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : box);
			}
		});

		loadCompanies();
	}

	private void loadCompanies() {
		companies.setAll(CompanyDAO.findAll());
		companyTable.setItems(companies);
	}

	@FXML
	private void handleNew() {
		ViewNavigator.loadView("CompanyFormView.fxml");
	}

	private void handleEdit(Company company) {
		ViewNavigator.loadView("CompanyFormView.fxml", company);
	}

	private void handleDelete(Company company) {
		if (CompanyDAO.deleteById(company.getId())) {
			companies.remove(company);
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete company");
			alert.showAndWait();
		}
	}
}
