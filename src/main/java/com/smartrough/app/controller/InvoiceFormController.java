package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.InvoiceDAO;
import com.smartrough.app.dao.InvoiceItemDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Invoice;
import com.smartrough.app.model.InvoiceItem;
import com.smartrough.app.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceFormController {

	@FXML
	private TextField invoiceNumberField;
	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<Company> companyComboBox;
	@FXML
	private ComboBox<Company> customerComboBox;

	@FXML
	private TableView<InvoiceItem> itemTable;
	@FXML
	private TableColumn<InvoiceItem, String> descriptionColumn;
	@FXML
	private TableColumn<InvoiceItem, BigDecimal> amountColumn;
	@FXML
	private TableColumn<InvoiceItem, Void> actionColumn;
	@FXML
	private TextField newDescriptionField;
	@FXML
	private TextField newAmountField;

	@FXML
	private TextField subtotalField;
	@FXML
	private TextField taxRateField;
	@FXML
	private TextField additionalCostsField;
	@FXML
	private TextField totalField;
	@FXML
	private TextArea notesField;

	private final ObservableList<InvoiceItem> items = FXCollections.observableArrayList();
	private Invoice invoiceBeingEdited;
	private boolean editing = false;

	@FXML
	public void initialize() {
		setupCompanyCombos();
		setupItemTable();

		if (!editing) {
			prepareNewInvoice();
		}
	}

	private void setupCompanyCombos() {
		companyComboBox.setCellFactory(cb -> new ListCell<>() {
			@Override
			protected void updateItem(Company c, boolean empty) {
				super.updateItem(c, empty);
				setText(empty || c == null ? null : c.getName());
			}
		});
		companyComboBox.setButtonCell(companyComboBox.getCellFactory().call(null));

		customerComboBox.setCellFactory(cb -> new ListCell<>() {
			@Override
			protected void updateItem(Company c, boolean empty) {
				super.updateItem(c, empty);
				setText(empty || c == null ? null : c.getName());
			}
		});
		customerComboBox.setButtonCell(customerComboBox.getCellFactory().call(null));

		companyComboBox.setItems(FXCollections.observableArrayList(CompanyDAO.findOwnCompany()));
		customerComboBox.setItems(FXCollections.observableArrayList(CompanyDAO.findAll()));
	}

	private void setupItemTable() {
		descriptionColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
		amountColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));

		actionColumn.setCellFactory(col -> new TableCell<>() {
			private final Button deleteButton = new Button();

			{
				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				deleteIcon.setFitHeight(16);
				deleteIcon.setFitWidth(16);
				deleteButton.setGraphic(deleteIcon);
				deleteButton.getStyleClass().add("icon-button");
				deleteButton.setTooltip(new Tooltip("Delete item"));

				deleteButton.setOnAction(e -> {
					InvoiceItem item = getTableView().getItems().get(getIndex());
					items.remove(item);
					recalculateTotals();
				});

				deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		});

		itemTable.setItems(items);
	}

	private void prepareNewInvoice() {
		datePicker.setValue(LocalDate.now());
		initializeInvoiceNumber();

		if (!companyComboBox.getItems().isEmpty()) {
			companyComboBox.getSelectionModel().select(0);
		}

		customerComboBox.getSelectionModel().clearSelection();

		items.clear();
		itemTable.getItems().clear();

		subtotalField.clear();
		taxRateField.clear();
		additionalCostsField.clear();
		totalField.clear();
		newDescriptionField.clear();
		newAmountField.clear();
		notesField.clear();

		invoiceBeingEdited = null;
		editing = false;
		invoiceNumberField.setDisable(false);
	}

	private void initializeInvoiceNumber() {
		long lastInvoiceNumber = InvoiceDAO.findLastInvoiceNumber();
		invoiceNumberField.setText(String.valueOf(Math.max(lastInvoiceNumber + 1, 300)));
	}

	public void loadInvoice(Invoice invoice) {
		this.invoiceBeingEdited = invoice;
		this.editing = true;

		invoiceNumberField.setText(invoice.getInvoiceNumber());
		invoiceNumberField.setDisable(true);
		datePicker.setValue(invoice.getDate());

		if (!companyComboBox.getItems().isEmpty()) {
			companyComboBox.getSelectionModel().select(0);
		}

		for (Company c : companyComboBox.getItems()) {
			if (c.getId() == invoice.getCompanyId()) {
				companyComboBox.getSelectionModel().select(c);
				break;
			}
		}

		for (Company c : customerComboBox.getItems()) {
			if (c.getId() == invoice.getCustomerId()) {
				customerComboBox.getSelectionModel().select(c);
				break;
			}
		}

		subtotalField.setText(invoice.getSubtotal() != null ? invoice.getSubtotal().toString() : "");
		taxRateField.setText(invoice.getTaxRate() != null ? invoice.getTaxRate().toString() : "");
		additionalCostsField
				.setText(invoice.getAdditionalCosts() != null ? invoice.getAdditionalCosts().toString() : "");
		totalField.setText(invoice.getTotal() != null ? invoice.getTotal().toString() : "");
		notesField.setText(invoice.getNotes() != null ? invoice.getNotes() : "");

		items.setAll(InvoiceItemDAO.findByInvoiceId(invoice.getId()));
	}

	@FXML
	private void handleAddItem() {
		String desc = newDescriptionField.getText();
		if (desc.isBlank()) {
			showAlert("Description cannot be empty.");
			return;
		}

		BigDecimal amt = BigDecimal.ZERO;
		if (!newAmountField.getText().isBlank()) {
			try {
				amt = new BigDecimal(newAmountField.getText());
			} catch (NumberFormatException e) {
				showAlert("Invalid amount. Leave blank if unknown.");
				return;
			}
		}

		InvoiceItem item = new InvoiceItem(null, null, desc, amt);
		items.add(item);

		newDescriptionField.clear();
		newAmountField.clear();
		recalculateTotals();
	}

	@FXML
	private void recalculateTotals() {
		if (items.isEmpty()) {
			subtotalField.clear();
			totalField.clear();
			return;
		}

		BigDecimal subtotal = items.stream().map(InvoiceItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		subtotalField.setText(subtotal.toString());

		BigDecimal tax = BigDecimal.ZERO;
		try {
			tax = subtotal.multiply(new BigDecimal(taxRateField.getText())).divide(BigDecimal.valueOf(100));
		} catch (Exception ignored) {
		}

		BigDecimal additional = BigDecimal.ZERO;
		try {
			additional = new BigDecimal(additionalCostsField.getText());
		} catch (Exception ignored) {
		}

		BigDecimal total = subtotal.add(tax).add(additional);
		totalField.setText(total.toString());
	}

	@FXML
	private void handleSave() {
		if (!validateForm()) {
			return;
		}

		Invoice invoice = invoiceBeingEdited != null ? invoiceBeingEdited : new Invoice();
		invoice.setInvoiceNumber(invoiceNumberField.getText());
		invoice.setDate(datePicker.getValue());
		invoice.setCompanyId(companyComboBox.getValue().getId());
		invoice.setCustomerId(customerComboBox.getValue().getId());
		invoice.setSubtotal(parseDecimalOrZero(subtotalField.getText()));
		invoice.setTaxRate(parseDecimalOrNull(taxRateField.getText()));
		invoice.setAdditionalCosts(parseDecimalOrNull(additionalCostsField.getText()));
		invoice.setTotal(parseDecimalOrZero(totalField.getText()));
		invoice.setNotes(notesField.getText());

		if (invoiceBeingEdited == null) {
			// Guardar factura nueva
			long invoiceId = InvoiceDAO.save(invoice);
			invoice.setId(invoiceId);

			// Guardar ítems asociados
			for (InvoiceItem item : items) {
				item.setInvoiceId(invoice.getId());
				InvoiceItemDAO.save(item);
			}

			prepareNewInvoice(); // limpiar formulario

		} else {
			// Actualizar factura existente
			InvoiceDAO.update(invoice);
			InvoiceItemDAO.delete(invoice.getId()); // eliminar ítems antiguos

			for (InvoiceItem item : items) {
				item.setInvoiceId(invoice.getId());
				InvoiceItemDAO.save(item);
			}

			handleCancel(); // volver al listado
		}
	}

	@FXML
	private void handleCancel() {
		editing = false;
		invoiceBeingEdited = null;
		ViewNavigator.loadView("InvoiceListView.fxml");
	}

	private void showAlert(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
	}

	private BigDecimal parseDecimalOrZero(String value) {
		if (value == null || value.isBlank())
			return BigDecimal.ZERO;
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			showAlert("Invalid number format: " + value);
			throw e;
		}
	}

	private BigDecimal parseDecimalOrNull(String value) {
		if (value == null || value.isBlank())
			return null;
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			showAlert("Invalid number format: " + value);
			throw e;
		}
	}

	private boolean validateForm() {
		if (invoiceNumberField.getText().isBlank()) {
			showAlert("Invoice number is required.");
			return false;
		}
		if (InvoiceDAO.existsInvoiceNumber(invoiceNumberField.getText()) && invoiceBeingEdited == null) {
			showAlert("Invoice number already exists.");
			return false;
		}
		if (datePicker.getValue() == null) {
			showAlert("Date is required.");
			return false;
		}
		if (companyComboBox.getValue() == null) {
			showAlert("Please select your company.");
			return false;
		}
		if (customerComboBox.getValue() == null) {
			showAlert("Please select a customer.");
			return false;
		}
		if (items.isEmpty()) {
			showAlert("Please add at least one item.");
			return false;
		}

		boolean hasAmount = items.stream()
				.anyMatch(item -> item.getAmount() != null && item.getAmount().compareTo(BigDecimal.ZERO) > 0);
		if (!hasAmount) {
			showAlert("At least one item must have a valid amount.");
			return false;
		}

		if (totalField.getText().isBlank() || new BigDecimal(totalField.getText()).compareTo(BigDecimal.ZERO) <= 0) {
			showAlert("Total cannot be blank or zero.");
			return false;
		}

		return true;
	}

}