package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.InvoiceDAO;
import com.smartrough.app.dao.InvoiceItemDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Invoice;
import com.smartrough.app.model.InvoiceItem;
import com.smartrough.app.util.InvoiceExporter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

	@FXML
	private void initializeInvoiceNumber() {
		long lastInvoiceNumber = InvoiceDAO.findLastInvoiceNumber();
		invoiceNumberField.setText(String.valueOf(Math.max(lastInvoiceNumber + 1, 300)));
	}

	@FXML
	public void initialize() {
		Company own = CompanyDAO.findOwnCompany();
		if (own != null) {
			companyComboBox.setItems(FXCollections.observableArrayList(own));
			companyComboBox.getSelectionModel().selectFirst();
		}

		List<Company> clients = CompanyDAO.findAll();
		customerComboBox.setItems(FXCollections.observableArrayList(clients));
		customerComboBox.getSelectionModel().clearSelection();

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

		descriptionColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
		amountColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));
		itemTable.setItems(items);

		datePicker.setValue(LocalDate.now());

		initializeInvoiceNumber();

		subtotalField.clear();
		totalField.clear();
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

		InvoiceItem item = new InvoiceItem();
		item.setDescription(desc);
		item.setAmount(amt);
		items.add(item);

		newDescriptionField.clear();
		newAmountField.clear();
		recalculateTotals();
	}

	@FXML
	private void recalculateTotals() {
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
		Invoice invoice = new Invoice();
		invoice.setInvoiceNumber(invoiceNumberField.getText());
		invoice.setDate(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());
		invoice.setCompanyId(companyComboBox.getValue().getId());
		invoice.setCustomerId(customerComboBox.getValue().getId());
		invoice.setSubtotal(parseDecimalOrZero(subtotalField.getText()));
		invoice.setTaxRate(parseDecimalOrNull(taxRateField.getText()));
		invoice.setAdditionalCosts(parseDecimalOrNull(additionalCostsField.getText()));
		invoice.setTotal(parseDecimalOrZero(totalField.getText()));
		invoice.setNotes(notesField.getText());

		long invoiceId = InvoiceDAO.save(invoice);
		invoice.setId(invoiceId);
		
		System.out.println("Fetching invoice with ID: " + invoiceId);

		for (InvoiceItem item : items) {
			item.setInvoiceId(invoiceId);
			InvoiceItemDAO.save(item);
		}

		showExportDialog(invoice);
		handleCancel();
	}

	@FXML
	private void handleCancel() {
		invoiceNumberField.clear();
		initializeInvoiceNumber();
		items.clear();
		subtotalField.clear();
		totalField.clear();
		taxRateField.clear();
		additionalCostsField.clear();
		newDescriptionField.clear();
		newAmountField.clear();
		notesField.clear();
		datePicker.setValue(LocalDate.now());
		customerComboBox.getSelectionModel().clearSelection();
	}

	private void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
		alert.showAndWait();
	}

	private BigDecimal parseDecimalOrZero(String value) {
		if (value == null || value.isBlank())
			return BigDecimal.ZERO;
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			showAlert("Invalid number format: " + value + ". Please check all numeric fields.");
			throw e;
		}
	}

	private BigDecimal parseDecimalOrNull(String value) {
		if (value == null || value.isBlank())
			return null;
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			showAlert("Invalid number format: " + value + ". Please check all numeric fields.");
			throw e;
		}
	}

	private void showExportDialog(Invoice invoice) {
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Export Invoice");

		CheckBox exportPdf = new CheckBox("Export as PDF");
		CheckBox exportWord = new CheckBox("Export as Word");

		VBox content = new VBox(10, exportPdf, exportWord);
		content.setPadding(new Insets(10));
		dialog.getDialogPane().setContent(content);

		ButtonType exportBtn = new ButtonType("Export", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(exportBtn, ButtonType.CANCEL);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == exportBtn) {
				if (exportPdf.isSelected()) {
					InvoiceExporter.exportToPdf(invoice);
				}
				if (exportWord.isSelected()) {
					InvoiceExporter.exportToWord(invoice);
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

}
