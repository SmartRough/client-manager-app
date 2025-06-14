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
		itemTable.setItems(items);
	}

	private void prepareNewInvoice() {
		datePicker.setValue(LocalDate.now());
		initializeInvoiceNumber();
		customerComboBox.getSelectionModel().clearSelection();
		items.clear();
		subtotalField.clear();
		taxRateField.clear();
		additionalCostsField.clear();
		totalField.clear();
		newDescriptionField.clear();
		newAmountField.clear();
		notesField.clear();
	}

	private void initializeInvoiceNumber() {
		long lastInvoiceNumber = InvoiceDAO.findLastInvoiceNumber();
		invoiceNumberField.setText(String.valueOf(Math.max(lastInvoiceNumber + 1, 300)));
	}

	public void loadInvoice(Invoice invoice) {
		this.invoiceBeingEdited = invoice;
		this.editing = true;

		invoiceNumberField.setText(invoice.getInvoiceNumber());
		datePicker.setValue(invoice.getDate());

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
		Invoice invoice = invoiceBeingEdited != null ? invoiceBeingEdited : new Invoice();
		invoice.setInvoiceNumber(invoiceNumberField.getText());
		invoice.setDate(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());
		invoice.setCompanyId(companyComboBox.getValue().getId());
		invoice.setCustomerId(customerComboBox.getValue().getId());
		invoice.setSubtotal(parseDecimalOrZero(subtotalField.getText()));
		invoice.setTaxRate(parseDecimalOrNull(taxRateField.getText()));
		invoice.setAdditionalCosts(parseDecimalOrNull(additionalCostsField.getText()));
		invoice.setTotal(parseDecimalOrZero(totalField.getText()));
		invoice.setNotes(notesField.getText());

		if (invoiceBeingEdited == null) {
			long invoiceId = InvoiceDAO.save(invoice);
			invoice.setId(invoiceId);
		} else {
			InvoiceDAO.update(invoice);
			InvoiceItemDAO.delete(invoice.getId());
		}

		for (InvoiceItem item : items) {
			item.setInvoiceId(invoice.getId());
			InvoiceItemDAO.save(item);
		}

		showExportDialog(invoice);
		handleCancel();
	}

	@FXML
	private void handleCancel() {
		editing = false;
		invoiceBeingEdited = null;
		prepareNewInvoice();
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

		dialog.setResultConverter(button -> {
			if (button == exportBtn) {
				if (exportPdf.isSelected())
					InvoiceExporter.exportToPdf(invoice);
				if (exportWord.isSelected())
					InvoiceExporter.exportToWord(invoice);
			}
			return null;
		});

		dialog.showAndWait();
	}
}
