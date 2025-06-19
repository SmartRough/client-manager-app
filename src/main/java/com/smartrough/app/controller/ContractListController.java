package com.smartrough.app.controller;

import com.smartrough.app.dao.ContractDAO;
import com.smartrough.app.model.Contract;
import com.smartrough.app.util.ContractExporter;
import com.smartrough.app.util.ViewNavigator;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ContractListController {

	@FXML
	private TableView<Contract> contractTable;
	@FXML
	private TableColumn<Contract, String> poCol;
	@FXML
	private TableColumn<Contract, String> startDateCol;
	@FXML
	private TableColumn<Contract, String> endDateCol;
	@FXML
	private TableColumn<Contract, String> ownerCol;
	@FXML
	private TableColumn<Contract, String> addressCol;
	@FXML
	private TableColumn<Contract, String> totalCol;
	@FXML
	private TableColumn<Contract, String> actionsCol;
	@FXML
	private TextField searchField;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	@FXML
	public void initialize() {
		poCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPoNumber()));
		startDateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getStartDate() != null ? data.getValue().getStartDate().format(formatter) : "-"));
		endDateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getEndDate() != null ? data.getValue().getEndDate().format(formatter) : "-"));
		ownerCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOwner1()
				+ (data.getValue().getOwner2() != null ? " & " + data.getValue().getOwner2() : "")));
		addressCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAddress()));
		totalCol.setCellValueFactory(
				data -> new ReadOnlyStringWrapper(String.format("$%.2f", data.getValue().getTotalPrice())));

		actionsCol.setCellFactory(col -> new TableCell<>() {
			private final Button editBtn = new Button();
			private final Button deleteBtn = new Button();
			private final HBox box = new HBox(5, editBtn, deleteBtn);

			{
				// Ícono de editar
				ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/edit.png")));
				editIcon.setFitHeight(16);
				editIcon.setFitWidth(16);
				editBtn.setGraphic(editIcon);
				editBtn.getStyleClass().add("icon-button");
				editBtn.setTooltip(new Tooltip("Edit Contract"));

				// Ícono de eliminar
				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				deleteIcon.setFitHeight(16);
				deleteIcon.setFitWidth(16);
				deleteBtn.setGraphic(deleteIcon);
				deleteBtn.getStyleClass().add("icon-button");
				deleteBtn.setTooltip(new Tooltip("Delete Contract"));

				// Acciones
				editBtn.setOnAction(e -> {
					Contract contract = getTableView().getItems().get(getIndex());
					ViewNavigator.loadView("ContractFormView.fxml", contract);
				});

				deleteBtn.setOnAction(e -> {
					Contract contract = getTableView().getItems().get(getIndex());
					deleteContractWithFiles(contract);
				});

				box.setStyle("-fx-alignment: center;");
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : box);
			}
		});

		loadContracts();
	}

	private void loadContracts() {
		List<Contract> list = ContractDAO.findAll();
		contractTable.getItems().setAll(list);
	}

	@FXML
	private void handleAdd() {
		ViewNavigator.loadView("ContractFormView.fxml");
	}

	@FXML
	private void handleExport() {
		Contract selected = contractTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No Selection");
			alert.setHeaderText(null);
			alert.setContentText("Please select a contract to export.");
			alert.showAndWait();
			return;
		}

		ContractExporter.exportToPdf(selected);
	}

	@FXML
	private void handleSendEmail() {

	}

	private void deleteContractWithFiles(Contract contract) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setHeaderText("Are you sure you want to delete this contract?");
		confirm.setContentText("This will permanently delete the contract and its attachments.");

		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// 1. Delete folder if exists
				if (contract.getMeasureDate() != null && contract.getPoNumber() != null) {
					String folderName = contract.getMeasureDate().toString();
					String poNumber = contract.getPoNumber().replaceAll("[^a-zA-Z0-9_\\-]", "_");
					File folder = new File(System.getProperty("user.dir"), "contracts/" + folderName + "/" + poNumber);
					if (folder.exists() && folder.isDirectory()) {
						for (File file : folder.listFiles()) {
							file.delete();
						}
						folder.delete(); // delete the folder after its contents
					}
				}

				// 2. Delete from database
				ContractDAO.delete(contract.getId());
				loadContracts(); // refresh table
			}
		});
	}

}
