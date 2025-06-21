package com.smartrough.app.controller;

import com.smartrough.app.dao.ContractDAO;
import com.smartrough.app.model.Contract;
import com.smartrough.app.util.ContractExporter;
import com.smartrough.app.util.ViewNavigator;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
	private TableColumn<Contract, String> measureDateCol;
	@FXML
	private TableColumn<Contract, String> startDateCol;
	@FXML
	private TableColumn<Contract, String> endDateCol;
	@FXML
	private TableColumn<Contract, String> ownerCol;
	@FXML
	private TableColumn<Contract, String> homePhoneCol;
	@FXML
	private TableColumn<Contract, String> totalCol;
	@FXML
	private TableColumn<Contract, String> actionsCol;
	@FXML
	private TextField searchField;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private ObservableList<Contract> masterList;
	private FilteredList<Contract> filteredData;

	@FXML
	public void initialize() {
		setupColumns();
		loadContracts();
		setupSearch();
		setupActionsColumn();
	}

	private void setupColumns() {
		poCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPoNumber()));
		measureDateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getMeasureDate() != null ? data.getValue().getMeasureDate().format(formatter) : "-"));
		startDateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getStartDate() != null ? data.getValue().getStartDate().format(formatter) : "-"));
		endDateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getEndDate() != null ? data.getValue().getEndDate().format(formatter) : "-"));
		ownerCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOwner1()
				+ (data.getValue().getOwner2() != null ? " & " + data.getValue().getOwner2() : "")));
		homePhoneCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getHomePhone() != null ? data.getValue().getHomePhone() : "-"));
		totalCol.setCellValueFactory(
				data -> new ReadOnlyStringWrapper(String.format("$%.2f", data.getValue().getTotalPrice())));
	}

	private void loadContracts() {
		List<Contract> contracts = ContractDAO.findAll();
		if (masterList == null) {
			masterList = FXCollections.observableArrayList(contracts);
			filteredData = new FilteredList<>(masterList, p -> true);
			contractTable.setItems(filteredData);
		} else {
			masterList.setAll(contracts); // Mantiene el filtro activo
		}
	}

	private void setupSearch() {
		searchField.textProperty().addListener((obs, oldVal, newVal) -> {
			String lower = newVal != null ? newVal.toLowerCase() : "";
			filteredData.setPredicate(contract -> {
				if (lower.isBlank())
					return true;
				return (contract.getPoNumber() != null && contract.getPoNumber().toLowerCase().contains(lower))
						|| (contract.getOwner1() != null && contract.getOwner1().toLowerCase().contains(lower))
						|| (contract.getOwner2() != null && contract.getOwner2().toLowerCase().contains(lower))
						|| (contract.getHomePhone() != null && contract.getHomePhone().toLowerCase().contains(lower));
			});
		});
	}

	private void setupActionsColumn() {
		actionsCol.setCellFactory(col -> new TableCell<>() {
			private final Button editBtn = new Button();
			private final Button deleteBtn = new Button();
			private final HBox box = new HBox(5, editBtn, deleteBtn);

			{
				editBtn.setGraphic(
						new ImageView(new Image(getClass().getResourceAsStream("/img/edit.png"), 16, 16, true, true)));
				editBtn.getStyleClass().add("icon-button");
				editBtn.setTooltip(new Tooltip("Edit Contract"));

				deleteBtn.setGraphic(new ImageView(
						new Image(getClass().getResourceAsStream("/img/delete.png"), 16, 16, true, true)));
				deleteBtn.getStyleClass().add("icon-button");
				deleteBtn.setTooltip(new Tooltip("Delete Contract"));

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
	}

	@FXML
	private void handleAdd() {
		ViewNavigator.loadView("ContractFormView.fxml");
	}

	@FXML
	private void handleExport() {
		Contract selected = contractTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("No Selection", "Please select a contract to export.");
			return;
		}
		ContractExporter.exportToPdf(selected);
	}

	@FXML
	private void handleSendEmail() {
		// Oculto de momento
	}

	private void deleteContractWithFiles(Contract contract) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setHeaderText("Are you sure you want to delete this contract?");
		confirm.setContentText("This will permanently delete the contract and its attachments.");

		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// 1. Eliminar archivos adjuntos del disco
				if (contract.getMeasureDate() != null && contract.getPoNumber() != null) {
					String folderName = contract.getMeasureDate().toString();
					String poNumber = contract.getPoNumber().replaceAll("[^a-zA-Z0-9_\\-]", "_");
					File folder = new File(System.getProperty("user.dir"), "contracts/" + folderName + "/" + poNumber);

					if (folder.exists() && folder.isDirectory()) {
						File[] files = folder.listFiles();
						if (files != null) {
							for (File file : files) {
								boolean deleted = file.delete();
								System.out.println("Deleted file: " + file.getName() + " => " + deleted);
							}
						}
						boolean deletedFolder = folder.delete();
						System.out.println("Deleted folder: " + folder.getAbsolutePath() + " => " + deletedFolder);

						// Limpiar carpeta del día si queda vacía
						File dateFolder = folder.getParentFile();
						if (dateFolder != null && dateFolder.isDirectory()) {
							File[] remaining = dateFolder.listFiles();
							if (remaining == null || remaining.length == 0) {
								boolean deletedDateFolder = dateFolder.delete();
								System.out.println("Deleted empty date folder: " + dateFolder.getAbsolutePath() + " => "
										+ deletedDateFolder);
							}
						}
					}
				}

				// 2. Eliminar contrato de la base de datos
				ContractDAO.delete(contract.getId());

				// 3. Actualizar tabla
				loadContracts();
			}
		});
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
