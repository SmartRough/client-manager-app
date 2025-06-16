package com.smartrough.app.controller;

import com.smartrough.app.dao.ContractDAO;
import com.smartrough.app.model.Contract;
import com.smartrough.app.util.ViewNavigator;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ContractListController {

	@FXML
	private TableView<Contract> contractTable;
	@FXML
	private TableColumn<Contract, String> poCol;
	@FXML
	private TableColumn<Contract, String> dateCol;
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
		dateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getDate() != null ? data.getValue().getDate().format(formatter) : "-"));
		ownerCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOwner1()
				+ (data.getValue().getOwner2() != null ? " & " + data.getValue().getOwner2() : "")));
		addressCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAddress()));
		totalCol.setCellValueFactory(
				data -> new ReadOnlyStringWrapper(String.format("$%.2f", data.getValue().getTotalPrice())));

		actionsCol.setCellFactory(col -> new TableCell<>() {
			private final Button editBtn = new Button("Edit");

			{
				editBtn.setOnAction(e -> {
					Contract contract = getTableView().getItems().get(getIndex());
					ViewNavigator.loadView("ContractFormView.fxml", contract);
				});
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					HBox box = new HBox(editBtn);
					box.setSpacing(5);
					setGraphic(box);
				}
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
		// Implementación futura
	}
}
