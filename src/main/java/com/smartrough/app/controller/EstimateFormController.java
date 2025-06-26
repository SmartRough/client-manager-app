package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.EstimateDAO;
import com.smartrough.app.dao.EstimateItemDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Estimate;
import com.smartrough.app.model.EstimateItem;
import com.smartrough.app.util.NumberFieldHelper;
import com.smartrough.app.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
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
	private TextField approvedByField;
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
	private final List<File> imageFiles = new ArrayList<>();

	private Estimate estimateBeingEdited;
	private boolean editing = false;

	@FXML
	public void initialize() {
		setupCompanyCombos();
		setupItemTable();
		prepareNewEstimate();

		NumberFieldHelper.applyDecimalFormat(totalField);
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
				deleteButton.setOnAction(e -> items.remove(getTableView().getItems().get(getIndex())));
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
		approvedByField.clear();
		jobDescriptionArea.clear();
		newDescriptionField.clear();
		imageNames.clear();
		imageFiles.clear();
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
		items.add(new EstimateItem(null, null, desc));
		newDescriptionField.clear();
	}

	@FXML
	private void handleAddImage() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select Images");
		chooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));

		List<File> selected = chooser.showOpenMultipleDialog(null);
		if (selected == null)
			return;

		imageFiles.clear();
		imageNames.clear();

		for (File file : selected) {
			imageFiles.add(file);
			imageNames.add(file.getName());
		}
		System.out.println("Nuevas imágenes seleccionadas. Se reemplazaron las anteriores.");
		imageCountLabel.setText(imageNames.size() + " images attached");
	}

	@FXML
	private void handleSave() {
		System.out.println("Iniciando guardado de estimado...");
		if (!validateForm()) {
			System.out.println("Validación fallida.");
			return;
		}

		Estimate estimate = editing ? estimateBeingEdited : new Estimate();
		estimate.setDate(datePicker.getValue());
		estimate.setCompanyId(companyComboBox.getValue().getId());
		estimate.setCustomerId(customerComboBox.getValue().getId());
		estimate.setApproved_by(approvedByField.getText());
		estimate.setJobDescription(jobDescriptionArea.getText());
		estimate.setTotal(NumberFieldHelper.parse(totalField.getText()));
		estimate.setItems(new ArrayList<>(items));
		estimate.setImageNames(new ArrayList<>(imageNames));

		String oldFolderPath = editing && estimateBeingEdited != null ? getEstimateFolderPath(estimateBeingEdited)
				: null;

		if (!editing) {
			System.out.println("Guardando nuevo estimado...");
			long id = EstimateDAO.save(estimate);
			estimate.setId(id);
			for (EstimateItem item : items) {
				item.setEstimateId(id);
				EstimateItemDAO.save(item);
			}
		} else {
			System.out.println("Actualizando estimado existente ID: " + estimate.getId());
			EstimateDAO.update(estimate);
			EstimateItemDAO.delete(estimate.getId());
			for (EstimateItem item : items) {
				item.setEstimateId(estimate.getId());
				EstimateItemDAO.save(item);
			}
		}

		// Guardar imágenes en carpeta
		String newFolderPath = getEstimateFolderPath(estimate);
		System.out.println("Ruta destino: " + newFolderPath);

		File newFolder = new File(newFolderPath);
		if (!newFolder.exists()) {
			System.out.println("Carpeta no existe. Creando...");
			boolean created = newFolder.mkdirs();
			System.out.println("Carpeta creada: " + created);
		}

		System.out.println("Archivos a copiar: " + imageFiles.size());
		for (File file : imageFiles) {
			try {
				System.out.println("Copiando archivo: " + file.getAbsolutePath());
				File dest = new File(newFolder, file.getName());
				System.out.println("Destino: " + dest.getAbsolutePath());

				if (!dest.exists() || !dest.getAbsolutePath().equals(file.getAbsolutePath())) {
					java.nio.file.Files.copy(file.toPath(), dest.toPath(),
							java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Copiado correctamente");
				} else {
					System.out.println("Archivo ya existe en destino, se omite.");
				}
			} catch (Exception ex) {
				System.out.println("ERROR al copiar archivo: " + file.getName());
				ex.printStackTrace();
				showAlert("Failed to copy image: " + file.getName());
			}
		}

		if (editing && oldFolderPath != null) {
			File oldFolder = new File(oldFolderPath);
			cleanupOldImages(oldFolder, imageNames);
			cleanupFolderIfEmpty(oldFolder);
		}

		System.out.println("Guardado completo. Limpiando imagenes temporales...");
		imageFiles.clear();
		imageNames.clear(); // ya están guardadas, no las necesitas más en memoria
		ViewNavigator.loadView("EstimateListView.fxml");
	}

	@FXML
	private void handleCancel() {
		if (!editing && !imageNames.isEmpty())
			cleanupTemporaryImages();
		imageFiles.clear();
		imageNames.clear();
		ViewNavigator.loadView("EstimateListView.fxml");
	}

	private boolean validateForm() {
		if (datePicker.getValue() == null)
			return showError("Date is required.");
		if (companyComboBox.getValue() == null || customerComboBox.getValue() == null)
			return showError("Company and customer must be selected.");
		if (approvedByField.getText().isBlank())
			return showError("Approved by is required.");
		if (items.isEmpty())
			return showError("At least one item is required.");
		if (imageFiles.isEmpty() && imageNames.isEmpty())
			return showError("At least one image must be attached.");
		if (totalField.getText().isBlank())
			return showError("Total is required.");
		try {
			NumberFieldHelper.parse(totalField.getText());
		} catch (Exception e) {
			return showError("Total must be a valid number.");
		}
		return true;
	}

	private boolean showError(String msg) {
		showAlert(msg);
		return false;
	}

	private void showAlert(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
	}

	public void loadEstimate(Estimate estimate) {
		this.estimateBeingEdited = estimate;
		this.editing = true;

		datePicker.setValue(estimate.getDate());
		companyComboBox.getItems().stream().filter(c -> c.getId() == estimate.getCompanyId()).findFirst()
				.ifPresent(companyComboBox.getSelectionModel()::select);
		customerComboBox.getItems().stream().filter(c -> c.getId() == estimate.getCustomerId()).findFirst()
				.ifPresent(customerComboBox.getSelectionModel()::select);
		approvedByField.setText(estimate.getApproved_by());
		jobDescriptionArea.setText(estimate.getJobDescription());
		totalField.setText(NumberFieldHelper.format(estimate.getTotal()));
		items.clear();
		items.setAll(EstimateItemDAO.findByEstimateId(estimate.getId()));

		imageNames.clear();
		imageNames.addAll(estimate.getImageNames());
		imageFiles.clear();
		String folderPath = getEstimateFolderPath(estimate);
		for (String name : imageNames) {
			File file = new File(folderPath, name);
			if (file.exists()) {
				imageFiles.add(file);
			}
		}
		imageCountLabel.setText(imageNames.size() + " images attached");
	}

	private String getEstimateFolderPath(Estimate estimate) {
		if (estimate.getDate() == null || estimate.getCustomerId() == null || estimate.getCompanyId() == null)
			return "";

		String folderName = estimate.getDate().toString();
		String customerName = "";
		for (Company c : customerComboBox.getItems()) {
			if (c.getId() == estimate.getCustomerId()) {
				customerName = c.getName().replaceAll("[^a-zA-Z0-9_\\-]", "_");
				break;
			}
		}
		if (customerName.isBlank())
			return "";

		// Nuevo formato: /estimates/YYYY-MM-DD/customer_name/contract_id
		return System.getProperty("user.dir") + File.separator + "estimates" + File.separator + folderName
				+ File.separator + customerName + File.separator + "estimates_" + estimate.getId();
	}

	private void cleanupOldImages(File folder, List<String> validNames) {
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (!validNames.contains(file.getName())) {
						file.delete();
					}
				}
			}
		}
	}

	private void cleanupTemporaryImages() {
		if (imageNames.isEmpty() || datePicker.getValue() == null || customerComboBox.getValue() == null)
			return;

		File folder = new File(getEstimateFolderPath(new Estimate() {
			{
				setDate(datePicker.getValue());
				setCustomerId(customerComboBox.getValue().getId());
			}
		}));

		for (String name : imageNames) {
			File file = new File(folder, name);
			if (file.exists())
				file.delete();
		}
		cleanupFolderIfEmpty(folder);
	}

	private void cleanupFolderIfEmpty(File folder) {
		if (folder.exists() && folder.isDirectory() && folder.listFiles() != null && folder.listFiles().length == 0) {
			folder.delete();
		}
	}

}
