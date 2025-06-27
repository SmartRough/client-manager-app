package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.InvoiceDAO;
import com.smartrough.app.dao.InvoiceItemDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Invoice;
import com.smartrough.app.model.InvoiceItem;
import com.smartrough.app.util.NumberFieldHelper;
import com.smartrough.app.util.ViewNavigator;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

		NumberFieldHelper.applyIntegerFormat(invoiceNumberField);
		NumberFieldHelper.applyDecimalFormat(newAmountField);
		NumberFieldHelper.applyDecimalFormat(subtotalField);
		NumberFieldHelper.applyDecimalFormat(taxRateField);
		NumberFieldHelper.applyDecimalFormat(additionalCostsField);
		NumberFieldHelper.applyDecimalFormat(totalField);

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
		descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
		amountColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAmount()));

		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
		amountColumn.setCellFactory(column -> new TableCell<>() {
			@Override
			protected void updateItem(BigDecimal amount, boolean empty) {
				super.updateItem(amount, empty);
				setText(empty || amount == null ? "" : currencyFormat.format(amount));
			}
		});

		actionColumn.setCellFactory(col -> new TableCell<>() {
			private final Button deleteButton = new Button();
			private final Button editButton = new Button();
			private final HBox buttons = new HBox(5);

			{
				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				deleteIcon.setFitWidth(16);
				deleteIcon.setFitHeight(16);
				deleteButton.setGraphic(deleteIcon);
				deleteButton.setStyle("-fx-background-color: transparent;");
				deleteButton.setOnAction(e -> {
					InvoiceItem item = getTableView().getItems().get(getIndex());
					items.remove(item);
					updateItemOrders();
					recalculateTotals();
				});

				ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/edit.png")));
				editIcon.setFitWidth(16);
				editIcon.setFitHeight(16);
				editButton.setGraphic(editIcon);
				editButton.setStyle("-fx-background-color: transparent;");
				editButton.setOnAction(e -> showEditItemDialog(getTableView().getItems().get(getIndex())));

				buttons.getChildren().addAll(editButton, deleteButton);
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : buttons);
			}
		});

		itemTable.setItems(items);
		enableItemDragAndDrop();
	}

	private void enableItemDragAndDrop() {
		itemTable.setRowFactory(tv -> {
			TableRow<InvoiceItem> row = new TableRow<>();

			row.setOnDragDetected(event -> {
				if (!row.isEmpty()) {
					Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
					ClipboardContent cc = new ClipboardContent();
					cc.putString(Integer.toString(row.getIndex()));
					db.setContent(cc);
					event.consume();
				}
			});

			row.setOnDragOver(event -> {
				if (event.getGestureSource() != row && event.getDragboard().hasString()) {
					event.acceptTransferModes(TransferMode.MOVE);
				}
				event.consume();
			});

			row.setOnDragDropped(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasString()) {
					int draggedIndex = Integer.parseInt(db.getString());
					InvoiceItem draggedItem = itemTable.getItems().remove(draggedIndex);
					int dropIndex = row.isEmpty() ? itemTable.getItems().size() : row.getIndex();
					itemTable.getItems().add(dropIndex, draggedItem);
					updateItemOrders();
					itemTable.refresh();
					event.setDropCompleted(true);
				}
				event.consume();
			});

			return row;
		});
	}

	private void updateItemOrders() {
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setOrder(i);
		}
	}

	private void showEditItemDialog(InvoiceItem item) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Edit Item");

		TextArea descriptionArea = new TextArea(item.getDescription());
		NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
		currencyFormat.setMinimumFractionDigits(2);
		currencyFormat.setMaximumFractionDigits(2);

		TextField amountField = new TextField(item.getAmount() != null ? currencyFormat.format(item.getAmount()) : "");
		NumberFieldHelper.applyDecimalFormat(amountField);

		VBox content = new VBox(10, new Label("Description:"), descriptionArea, new Label("Amount:"), amountField);
		dialog.getDialogPane().setContent(content);

		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				item.setDescription(descriptionArea.getText());
				try {
					item.setAmount(new BigDecimal(amountField.getText()));
				} catch (NumberFormatException e) {
					showAlert("Invalid amount.");
				}
				recalculateTotals();
				itemTable.refresh();
			}
			return null;
		});

		dialog.showAndWait();
	}

	private void prepareNewInvoice() {
		editing = false;
		invoiceBeingEdited = null;
		datePicker.setValue(LocalDate.now());
		initializeInvoiceNumber();
		if (!companyComboBox.getItems().isEmpty())
			companyComboBox.getSelectionModel().select(0);
		customerComboBox.getSelectionModel().clearSelection();

		items.clear();
		itemTable.refresh();

		subtotalField.clear();
		taxRateField.clear();
		additionalCostsField.clear();
		totalField.clear();
		newDescriptionField.clear();
		newAmountField.clear();
		notesField.clear();
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

		companyComboBox.getItems().stream().filter(c -> c.getId() == invoice.getCompanyId()).findFirst()
				.ifPresent(companyComboBox.getSelectionModel()::select);

		customerComboBox.getItems().stream().filter(c -> c.getId() == invoice.getCustomerId()).findFirst()
				.ifPresent(customerComboBox.getSelectionModel()::select);

		subtotalField.setText(NumberFieldHelper.format(invoice.getSubtotal()));
		taxRateField.setText(invoice.getTaxRate() != null ? invoice.getTaxRate().toString() : "");
		additionalCostsField.setText(NumberFieldHelper.format(invoice.getAdditionalCosts()));
		totalField.setText(NumberFieldHelper.format(invoice.getTotal()));
		notesField.setText(invoice.getNotes() != null ? invoice.getNotes() : "");

		items.clear();
		items.addAll(InvoiceItemDAO.findByInvoiceId(invoice.getId()));
		itemTable.refresh();
	}

	@FXML
	private void handleAddItem() {
		String desc = newDescriptionField.getText();
		if (desc.isBlank()) {
			showAlert("Description cannot be empty.");
			return;
		}

		BigDecimal amt;
		try {
			amt = newAmountField.getText().isBlank() ? BigDecimal.ZERO : new BigDecimal(newAmountField.getText());
		} catch (NumberFormatException e) {
			showAlert("Invalid amount.");
			return;
		}

		int nextOrder = items.size();
		InvoiceItem newItem = new InvoiceItem(null, null, desc, amt, nextOrder);
		newItem.setOrder(nextOrder);

		items.add(newItem);
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
		subtotalField.setText(NumberFieldHelper.format(subtotal));

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
		totalField.setText(NumberFieldHelper.format(total));
	}

	@FXML
	private void handleSave() {
		if (!validateForm())
			return;

		updateItemOrders(); // asegúrate de tener orden correcto

		Invoice invoice = editing ? invoiceBeingEdited : new Invoice();
		invoice.setInvoiceNumber(invoiceNumberField.getText());
		invoice.setDate(datePicker.getValue());
		invoice.setCompanyId(companyComboBox.getValue().getId());
		invoice.setCustomerId(customerComboBox.getValue().getId());
		invoice.setSubtotal(NumberFieldHelper.parse(subtotalField.getText()));
		invoice.setTaxRate(parseDecimalOrNull(taxRateField.getText()));
		invoice.setAdditionalCosts(NumberFieldHelper.parse(additionalCostsField.getText()));
		invoice.setTotal(NumberFieldHelper.parse(totalField.getText()));
		invoice.setNotes(notesField.getText());

		if (!editing) {
			long id = InvoiceDAO.save(invoice);
			invoice.setId(id);
			for (InvoiceItem item : items) {
				item.setInvoiceId(id);
				InvoiceItemDAO.save(item);
			}
		} else {
			InvoiceDAO.update(invoice);
			List<InvoiceItem> existingItems = InvoiceItemDAO.findByInvoiceId(invoice.getId());
			Map<Long, InvoiceItem> existingMap = new java.util.HashMap<>();
			for (InvoiceItem it : existingItems) {
				if (it.getId() != null)
					existingMap.put(it.getId(), it);
			}

			for (InvoiceItem item : items) {
				item.setInvoiceId(invoice.getId());
				if (item.getId() == null) {
					InvoiceItemDAO.save(item);
				} else if (existingMap.containsKey(item.getId())) {
					InvoiceItem original = existingMap.get(item.getId());
					if (!item.getDescription().equals(original.getDescription())
							|| item.getAmount().compareTo(original.getAmount()) != 0
							|| item.getOrder() != original.getOrder()) {
						InvoiceItemDAO.update(item);
					}
					existingMap.remove(item.getId());
				}
			}

			for (Long removedId : existingMap.keySet()) {
				InvoiceItemDAO.delete(removedId);
			}
		}

		ViewNavigator.loadView("InvoiceListView.fxml");
	}

	@FXML
	private void handleCancel() {
		ViewNavigator.loadView("InvoiceListView.fxml");
	}

	private void showAlert(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
	}

	private BigDecimal parseDecimalOrNull(String value) {
		if (value == null || value.isBlank())
			return null;
		return NumberFieldHelper.parse(value);
	}

	private boolean validateForm() {
		if (invoiceNumberField.getText().isBlank()) {
			showAlert("Invoice number is required.");
			return false;
		}
		if (!editing && InvoiceDAO.existsInvoiceNumber(invoiceNumberField.getText())) {
			showAlert("Invoice number already exists.");
			return false;
		}
		if (datePicker.getValue() == null) {
			showAlert("Date is required.");
			return false;
		}
		if (companyComboBox.getValue() == null || customerComboBox.getValue() == null) {
			showAlert("Company and customer are required.");
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
		BigDecimal total = NumberFieldHelper.parse(totalField.getText());
		if (total.compareTo(BigDecimal.ZERO) <= 0) {
			showAlert("Total must be greater than zero.");
			return false;
		}
		return true;
	}
}
