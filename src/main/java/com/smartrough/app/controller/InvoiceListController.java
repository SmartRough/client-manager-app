package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.InvoiceDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Invoice;
import com.smartrough.app.util.ViewNavigator;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InvoiceListController {

	@FXML
	private TableView<Invoice> invoiceTable;
	@FXML
	private TableColumn<Invoice, String> numberCol;
	@FXML
	private TableColumn<Invoice, String> dateCol;
	@FXML
	private TableColumn<Invoice, String> clientCol;
	@FXML
	private TableColumn<Invoice, String> subtotalCol;
	@FXML
	private TableColumn<Invoice, String> taxCol;
	@FXML
	private TableColumn<Invoice, String> additionalCol;
	@FXML
	private TableColumn<Invoice, String> totalCol;
	@FXML
	private TableColumn<Invoice, Void> actionCol;

	@FXML
	private TextField searchField;
	@FXML
	private Button btnExport;
	@FXML
	private Button btnSendEmail;

	private final ObservableList<Invoice> invoices = FXCollections.observableArrayList();
	private final ObservableList<Invoice> filteredInvoices = FXCollections.observableArrayList();
	private final Map<Long, String> customerNamesCache = new HashMap<>();

	@FXML
	public void initialize() {
		numberCol.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));

		dateCol.setCellValueFactory(cellData -> {
			if (cellData.getValue().getDate() != null) {
				String formatted = cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
				return new ReadOnlyStringWrapper(formatted);
			}
			return new ReadOnlyStringWrapper("-");
		});

		clientCol.setCellValueFactory(cellData -> {
			Long customerId = cellData.getValue().getCustomerId();
			if (customerId != null) {
				String name = customerNamesCache.computeIfAbsent(customerId, id -> {
					Company company = CompanyDAO.findById(id);
					return company != null ? company.getName() : "Unknown";
				});
				return new ReadOnlyStringWrapper(name);
			}
			return new ReadOnlyStringWrapper("-");
		});

		subtotalCol.setCellValueFactory(cellData -> {
			if (cellData.getValue().getSubtotal() != null) {
				return new ReadOnlyStringWrapper(
						"$" + cellData.getValue().getSubtotal().setScale(2, RoundingMode.HALF_UP));
			}
			return new ReadOnlyStringWrapper("");
		});

		taxCol.setCellValueFactory(cellData -> {
			if (cellData.getValue().getTaxRate() != null) {
				return new ReadOnlyStringWrapper(
						cellData.getValue().getTaxRate().setScale(2, RoundingMode.HALF_UP) + "%");
			}
			return new ReadOnlyStringWrapper("");
		});

		additionalCol.setCellValueFactory(cellData -> {
			if (cellData.getValue().getAdditionalCosts() != null) {
				return new ReadOnlyStringWrapper(
						"$" + cellData.getValue().getAdditionalCosts().setScale(2, RoundingMode.HALF_UP));
			}
			return new ReadOnlyStringWrapper("");
		});

		totalCol.setCellValueFactory(cellData -> {
			if (cellData.getValue().getTotal() != null) {
				return new ReadOnlyStringWrapper(
						"$" + cellData.getValue().getTotal().setScale(2, RoundingMode.HALF_UP));
			}
			return new ReadOnlyStringWrapper("");
		});

		actionCol.setCellFactory(col -> new TableCell<>() {
			private final Button editBtn = new Button();
			private final Button deleteBtn = new Button();
			private final HBox box = new HBox(5, editBtn, deleteBtn);

			{
				ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/edit.png")));
				editIcon.setFitHeight(16);
				editIcon.setFitWidth(16);
				editBtn.setGraphic(editIcon);
				editBtn.getStyleClass().add("icon-button");
				editBtn.setTooltip(new Tooltip("Edit Invoice"));

				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				deleteIcon.setFitHeight(16);
				deleteIcon.setFitWidth(16);
				deleteBtn.setGraphic(deleteIcon);
				deleteBtn.getStyleClass().add("icon-button");
				deleteBtn.setTooltip(new Tooltip("Delete Invoice"));

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

		searchField.textProperty().addListener((obs, oldVal, newVal) -> filterInvoices(newVal));
		btnExport.setOnAction(e -> handleExport());
		btnSendEmail.setOnAction(e -> handleSendEmail());

		loadInvoices();
	}

	private void loadInvoices() {
		invoices.setAll(InvoiceDAO.findAll());
		filteredInvoices.setAll(invoices);
		invoiceTable.setItems(filteredInvoices);
	}

	private void filterInvoices(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			filteredInvoices.setAll(invoices);
		} else {
			String lower = keyword.toLowerCase();
			filteredInvoices.setAll(invoices.stream()
					.filter(i -> (i.getInvoiceNumber() != null && i.getInvoiceNumber().toLowerCase().contains(lower))
							|| (customerNamesCache.containsKey(i.getCustomerId())
									&& customerNamesCache.get(i.getCustomerId()).toLowerCase().contains(lower)))
					.collect(Collectors.toList()));
		}
	}

	@FXML
	private void handleNew() {
		ViewNavigator.loadView("InvoiceFormView.fxml");
	}

	@FXML
	private void handleSendEmail() {
		Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			new Alert(Alert.AlertType.INFORMATION, "Please select an invoice to send by email.").showAndWait();
			return;
		}
	}

	@FXML
	private void handleExport() {
		Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			new Alert(Alert.AlertType.INFORMATION, "Please select an invoice to export.").showAndWait();
			return;
		}
	}

	private void handleEdit(Invoice invoice) {
		ViewNavigator.loadView("InvoiceFormView.fxml", invoice);
	}

	private void handleDelete(Invoice invoice) {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Delete Confirmation");
		confirmation.setHeaderText("Are you sure you want to delete this invoice?");
		confirmation.setContentText("This action cannot be undone.");

		ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
		confirmation.getButtonTypes().setAll(yesBtn, noBtn);

		confirmation.showAndWait().ifPresent(response -> {
			if (response == yesBtn) {
				boolean success = InvoiceDAO.delete(invoice.getId());
				if (success) {
					invoices.remove(invoice);
					filteredInvoices.remove(invoice);
				} else {
					new Alert(Alert.AlertType.ERROR, "Failed to delete invoice.").showAndWait();
				}
			}
		});
	}
}