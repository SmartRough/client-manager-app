package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.EstimateDAO;
import com.smartrough.app.dao.EstimateItemDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Estimate;
import com.smartrough.app.model.EstimateItem;
import com.smartrough.app.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstimateFormController {

	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<Company> companyComboBox;
	@FXML
	private ComboBox<Company> customerComboBox;
	@FXML
	private TextArea jobDescriptionArea;

	@FXML
	private TableView<EstimateItem> itemTable;
	@FXML
	private TableColumn<EstimateItem, String> descriptionColumn;
	@FXML
	private TableColumn<EstimateItem, Void> actionColumn;

	@FXML
	private TextField newDescriptionField;
	@FXML
	private Label imageCountLabel;
	@FXML
	private TextField totalField;

	private final ObservableList<EstimateItem> items = FXCollections.observableArrayList();
	private final List<String> imageNames = new ArrayList<>();
	private Estimate estimateBeingEdited;
	private boolean editing = false;

	@FXML
	public void initialize() {
		setupCompanyCombos();
		setupItemTable();
		prepareNewEstimate();
	}

	private void setupCompanyCombos() {
		companyComboBox.setItems(FXCollections.observableArrayList(CompanyDAO.findOwnCompany()));
		customerComboBox.setItems(FXCollections.observableArrayList(CompanyDAO.findAll()));

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
	}

	private void setupItemTable() {
		descriptionColumn.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));

		actionColumn.setCellFactory(col -> new TableCell<>() {
			private final Button deleteButton = new Button();
			{
				ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				icon.setFitHeight(16);
				icon.setFitWidth(16);
				deleteButton.setGraphic(icon);
				deleteButton.getStyleClass().add("icon-button");
				deleteButton.setOnAction(e -> {
					EstimateItem item = getTableView().getItems().get(getIndex());
					items.remove(item);
					recalculateTotal();
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		});

		itemTable.setItems(items);
	}

	private void prepareNewEstimate() {
		editing = false;
		estimateBeingEdited = null;
		datePicker.setValue(LocalDate.now());
		if (!companyComboBox.getItems().isEmpty())
			companyComboBox.getSelectionModel().select(0);
		customerComboBox.getSelectionModel().clearSelection();
		jobDescriptionArea.clear();
		newDescriptionField.clear();
		imageNames.clear();
		items.clear();
		imageCountLabel.setText("0 images attached");
		totalField.clear();
	}

	@FXML
	private void handleAddItem() {
		String desc = newDescriptionField.getText();
		if (desc.isBlank()) {
			showAlert("Description is required.");
			return;
		}
		EstimateItem item = new EstimateItem(null, null, desc);
		items.add(item);
		newDescriptionField.clear();
		recalculateTotal();
	}

	@FXML
	private void handleAddImage() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select Images");
		chooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
		List<File> files = chooser.showOpenMultipleDialog(null);
		if (files != null) {
			for (File file : files) {
				imageNames.add(file.getName()); // puedes guardar path si quieres
			}
			imageCountLabel.setText(imageNames.size() + " images attached");
		}
	}

	private void recalculateTotal() {
		// Si en el futuro tienes montos, cambia esto
		BigDecimal total = new BigDecimal(items.size());
		totalField.setText(total.toString());
	}

	@FXML
	private void handleSave() {
		if (!validateForm())
			return;

		Estimate estimate = editing ? estimateBeingEdited : new Estimate();
		estimate.setDate(datePicker.getValue());
		estimate.setCompanyId(companyComboBox.getValue().getId());
		estimate.setCustomerId(customerComboBox.getValue().getId());
		estimate.setJobDescription(jobDescriptionArea.getText());
		estimate.setTotal(new BigDecimal(totalField.getText()));
		estimate.setItems(new ArrayList<>(items));
		estimate.setImageNames(new ArrayList<>(imageNames));

		if (!editing) {
			long id = EstimateDAO.save(estimate);
			estimate.setId(id);
			for (EstimateItem item : items) {
				item.setEstimateId(id);
				EstimateItemDAO.save(item);
			}
			prepareNewEstimate();
		} else {
			EstimateDAO.update(estimate);
			EstimateItemDAO.delete(estimate.getId());
			for (EstimateItem item : items) {
				item.setEstimateId(estimate.getId());
				EstimateItemDAO.save(item);
			}
			handleCancel();
		}
	}

	@FXML
	private void handleCancel() {
		ViewNavigator.loadView("EstimateListView.fxml");
	}

	private boolean validateForm() {
		if (datePicker.getValue() == null) {
			showAlert("Date is required.");
			return false;
		}
		if (companyComboBox.getValue() == null || customerComboBox.getValue() == null) {
			showAlert("Company and customer must be selected.");
			return false;
		}
		if (items.isEmpty()) {
			showAlert("At least one item is required.");
			return false;
		}
		return true;
	}

	private void showAlert(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
	}

	public void loadEstimate(Estimate estimate) {
		this.estimateBeingEdited = estimate;
		this.editing = true;

		datePicker.setValue(estimate.getDate());

		// Seleccionar la compañía
		for (Company c : companyComboBox.getItems()) {
			if (c.getId() == estimate.getCompanyId()) {
				companyComboBox.getSelectionModel().select(c);
				break;
			}
		}

		// Seleccionar el cliente
		for (Company c : customerComboBox.getItems()) {
			if (c.getId() == estimate.getCustomerId()) {
				customerComboBox.getSelectionModel().select(c);
				break;
			}
		}

		jobDescriptionArea.setText(estimate.getJobDescription());
		totalField.setText(estimate.getTotal().toString());
		items.setAll(EstimateItemDAO.findByEstimateId(estimate.getId()));
		imageNames.clear();
		imageNames.addAll(estimate.getImageNames());
		imageCountLabel.setText(imageNames.size() + " images attached");
	}
}