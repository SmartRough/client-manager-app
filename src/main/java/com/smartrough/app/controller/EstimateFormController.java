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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.layout.HBox;

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
	private TextField totalField;
	@FXML
	private TableView<String> imageTable;
	@FXML
	private TableColumn<String, String> imageNameColumn;
	@FXML
	private TableColumn<String, Void> imageActionColumn;

	private final ObservableList<String> imageObservableNames = FXCollections.observableArrayList();
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
		setupImageTable();

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
			private final Button editButton = new Button();
			private final HBox buttonBox = new HBox(5);

			{
				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				deleteIcon.setFitHeight(16);
				deleteIcon.setFitWidth(16);
				deleteButton.setGraphic(deleteIcon);
				deleteButton.getStyleClass().add("icon-button");

				ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/edit.png")));
				editIcon.setFitHeight(16);
				editIcon.setFitWidth(16);
				editButton.setGraphic(editIcon);
				editButton.getStyleClass().add("icon-button");

				deleteButton.setOnAction(e -> {
					items.remove(getTableView().getItems().get(getIndex()));
					updateItemOrders();
				});

				editButton.setOnAction(e -> {
					EstimateItem item = getTableView().getItems().get(getIndex());
					showEditItemDialog(item);
				});

				buttonBox.getChildren().addAll(editButton, deleteButton);
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : buttonBox);
			}
		});

		itemTable.setItems(items);
		enableItemDragAndDrop();
	}

	private void setupImageTable() {
		imageNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));

		imageActionColumn.setCellFactory(col -> new TableCell<>() {
			private final Button deleteButton = new Button("Delete");

			{
				deleteButton.setOnAction(e -> {
					String filename = getTableView().getItems().get(getIndex());
					int index = imageObservableNames.indexOf(filename);
					if (index >= 0) {
						imageObservableNames.remove(index);
						imageFiles.remove(index);
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		});

		imageTable.setItems(imageObservableNames);
	}

	private void enableItemDragAndDrop() {
		itemTable.setRowFactory(tv -> {
			TableRow<EstimateItem> row = new TableRow<>();

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
					EstimateItem draggedItem = itemTable.getItems().remove(draggedIndex);

					int dropIndex = row.isEmpty() ? itemTable.getItems().size() : row.getIndex();
					itemTable.getItems().add(dropIndex, draggedItem);

					event.setDropCompleted(true);
					itemTable.refresh();
					updateItemOrders();
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

	private void showEditItemDialog(EstimateItem item) {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Edit Description");
		dialog.setHeaderText("Edit the item description:");

		ButtonType okButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

		TextArea textArea = new TextArea(item.getDescription());
		textArea.setWrapText(true);
		textArea.setPrefRowCount(5);
		dialog.getDialogPane().setContent(textArea);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButton) {
				return textArea.getText();
			}
			return null;
		});

		dialog.showAndWait().ifPresent(newDesc -> {
			if (!newDesc.isBlank()) {
				item.setDescription(newDesc);
				itemTable.refresh();
			}
		});
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
		totalField.clear();
	}

	@FXML
	private void handleAddItem() {
		String desc = newDescriptionField.getText();
		if (desc.isBlank()) {
			showAlert("Description is required.");
			return;
		}
		int nextOrder = items.size();
		EstimateItem newItem = new EstimateItem(null, null, desc, nextOrder);
		newItem.setOrder(nextOrder);

		items.add(newItem);
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

		for (File file : selected) {
			if (!imageNames.contains(file.getName())) {
				imageFiles.add(file);
				imageObservableNames.add(file.getName());
				imageNames.add(file.getName());
			}
		}
	}

	@FXML
	private void handleSave() {
		System.out.println("Iniciando guardado de estimado...");
		if (!validateForm()) {
			System.out.println("Validación fallida.");
			return;
		}
		updateItemOrders();

		Estimate estimate = editing ? estimateBeingEdited : new Estimate();
		estimate.setDate(datePicker.getValue());
		estimate.setCompanyId(companyComboBox.getValue().getId());
		estimate.setCustomerId(customerComboBox.getValue().getId());
		estimate.setApproved_by(approvedByField.getText());
		estimate.setJobDescription(jobDescriptionArea.getText());
		estimate.setTotal(NumberFieldHelper.parse(totalField.getText()));
		estimate.setItems(new ArrayList<>(items));
		estimate.setImageNames(new ArrayList<>(imageObservableNames));

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
			List<EstimateItem> existingItems = EstimateItemDAO.findByEstimateId(estimate.getId());
			Map<Long, EstimateItem> existingMap = new java.util.HashMap<>();
			for (EstimateItem it : existingItems) {
				if (it.getId() != null)
					existingMap.put(it.getId(), it);
			}

			for (EstimateItem current : items) {
				current.setEstimateId(estimate.getId());

				if (current.getId() == null) {
					EstimateItemDAO.save(current);
				} else if (existingMap.containsKey(current.getId())) {
					EstimateItem original = existingMap.get(current.getId());
					if (!original.getDescription().equals(current.getDescription())
							|| original.getOrder() != current.getOrder()) {
						EstimateItemDAO.update(current);
					}
					existingMap.remove(current.getId()); // ya fue manejado
				}
			}

			// lo que quedó en el mapa son ítems eliminados
			for (Long id : existingMap.keySet()) {
				EstimateItemDAO.deleteById(id);
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
			cleanupOldImages(oldFolder, new ArrayList<>(imageObservableNames));
			cleanupFolderIfEmpty(oldFolder);
		}

		System.out.println("Guardado completo. Limpiando imagenes temporales...");
		imageFiles.clear();
		imageNames.clear(); // ya están guardadas, no las necesitas más en memoria
		ViewNavigator.loadView("EstimateListView.fxml");
	}

	@FXML
	private void handleCancel() {
		if (!editing && !imageObservableNames.isEmpty())
			cleanupTemporaryImages();
		imageFiles.clear();
		imageNames.clear();
		ViewNavigator.loadView("EstimateListView.fxml");
	}

	@FXML
	private void handleClearImages() {
		imageFiles.clear();
		imageNames.clear();
		imageObservableNames.clear();
		System.out.println("✅ Todas las imágenes fueron eliminadas.");
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
		imageObservableNames.clear();
		imageFiles.clear();

		imageNames.addAll(estimate.getImageNames());
		imageObservableNames.addAll(imageNames);

		String folderPath = getEstimateFolderPath(estimate);
		for (String name : imageNames) {
			File file = new File(folderPath, name);
			if (file.exists()) {
				imageFiles.add(file);
			}
		}
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
		if (imageObservableNames.isEmpty() || datePicker.getValue() == null || customerComboBox.getValue() == null)
			return;

		File folder = new File(getEstimateFolderPath(new Estimate() {
			{
				setDate(datePicker.getValue());
				setCustomerId(customerComboBox.getValue().getId());
			}
		}));

		for (String name : imageObservableNames) {
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
