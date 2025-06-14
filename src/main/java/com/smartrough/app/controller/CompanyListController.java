package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.util.ViewNavigator;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
	private TableColumn<Company, String> typeCol;
	@FXML
	private TableColumn<Company, Void> actionCol;
	@FXML
	private TextField searchField;

	private final ObservableList<Company> companies = FXCollections.observableArrayList();
	private FilteredList<Company> filteredCompanies;

	@FXML
	public void initialize() {
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		repCol.setCellValueFactory(new PropertyValueFactory<>("representative"));
		phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		typeCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getType().name()));

		actionCol.setCellFactory(col -> new TableCell<>() {
			private final Button editBtn = new Button();
			private final Button deleteBtn = new Button();
			private final HBox box = new HBox(5, editBtn, deleteBtn);

			{
				editBtn.setGraphic(
						new ImageView(new Image(getClass().getResourceAsStream("/img/edit.png"), 16, 16, true, true)));
				editBtn.getStyleClass().add("icon-button");
				editBtn.setTooltip(new Tooltip("Edit"));

				deleteBtn.setGraphic(new ImageView(
						new Image(getClass().getResourceAsStream("/img/delete.png"), 16, 16, true, true)));
				deleteBtn.getStyleClass().add("icon-button");
				deleteBtn.setTooltip(new Tooltip("Delete"));

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

		// Buscador
		searchField.textProperty().addListener((obs, old, newValue) -> {
			filteredCompanies.setPredicate(company -> {
				if (newValue == null || newValue.isBlank())
					return true;
				return company.getName().toLowerCase().contains(newValue.toLowerCase());
			});
		});
	}

	private void loadCompanies() {
		companies.setAll(CompanyDAO.findAll());
		filteredCompanies = new FilteredList<>(companies, p -> true);
		companyTable.setItems(filteredCompanies);
	}

	@FXML
	private void handleNew() {
		ViewNavigator.loadView("CompanyFormView.fxml");
	}

	private void handleEdit(Company company) {
		ViewNavigator.loadView("CompanyFormView.fxml", company);
	}

	private void handleDelete(Company company) {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Delete Confirmation");
		confirmation.setHeaderText("Are you sure you want to delete this company?");
		confirmation.setContentText("This action cannot be undone.");

		ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
		confirmation.getButtonTypes().setAll(yesBtn, noBtn);

		confirmation.showAndWait().ifPresent(response -> {
			if (response == yesBtn) {
				boolean success = CompanyDAO.deleteById(company.getId());
				if (success) {
					companies.remove(company);
				} else {
					new Alert(Alert.AlertType.ERROR, "Failed to delete company.").showAndWait();
				}
			}
		});
	}
}
