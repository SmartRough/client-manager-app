package com.smartrough.app.controller;

import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.EstimateDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Estimate;
import com.smartrough.app.util.EstimateExporter;
import com.smartrough.app.util.ViewNavigator;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EstimateListController {

	@FXML
	private TableView<Estimate> estimateTable;
	@FXML
	private TableColumn<Estimate, String> dateCol;
	@FXML
	private TableColumn<Estimate, String> clientCol;
	@FXML
	private TableColumn<Estimate, String> jobCol;
	@FXML
	private TableColumn<Estimate, String> totalCol;
	@FXML
	private TableColumn<Estimate, Void> actionCol;

	@FXML
	private TextField searchField;
	@FXML
	private Button btnExport;
	@FXML
	private Button btnSendEmail;

	private final ObservableList<Estimate> estimates = FXCollections.observableArrayList();
	private final ObservableList<Estimate> filteredEstimates = FXCollections.observableArrayList();
	private final Map<Long, String> customerNamesCache = new HashMap<>();

	@FXML
	public void initialize() {
		dateCol.setCellValueFactory(e -> {
			if (e.getValue().getDate() != null) {
				String formatted = e.getValue().getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
				return new ReadOnlyStringWrapper(formatted);
			}
			return new ReadOnlyStringWrapper("-");
		});

		clientCol.setCellValueFactory(e -> {
			Long customerId = e.getValue().getCustomerId();
			if (customerId != null) {
				String name = customerNamesCache.computeIfAbsent(customerId, id -> {
					Company company = CompanyDAO.findById(id);
					return company != null ? company.getName() : "Unknown";
				});
				return new ReadOnlyStringWrapper(name);
			}
			return new ReadOnlyStringWrapper("-");
		});

		jobCol.setCellValueFactory(
				e -> new ReadOnlyStringWrapper(Optional.ofNullable(e.getValue().getJobDescription()).orElse("-")));

		totalCol.setCellValueFactory(e -> {
			if (e.getValue().getTotal() != null) {
				return new ReadOnlyStringWrapper("$" + e.getValue().getTotal().setScale(2, RoundingMode.HALF_UP));
			}
			return new ReadOnlyStringWrapper("-");
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
				editBtn.setTooltip(new Tooltip("Edit Estimate"));

				ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				deleteIcon.setFitHeight(16);
				deleteIcon.setFitWidth(16);
				deleteBtn.setGraphic(deleteIcon);
				deleteBtn.getStyleClass().add("icon-button");
				deleteBtn.setTooltip(new Tooltip("Delete Estimate"));

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

		searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEstimates(newVal));
		btnExport.setOnAction(e -> handleExport());
		btnSendEmail.setOnAction(e -> handleSendEmail());

		loadEstimates();
	}

	private void loadEstimates() {
		estimates.setAll(EstimateDAO.findAll());
		filteredEstimates.setAll(estimates);
		estimateTable.setItems(filteredEstimates);
	}

	private void filterEstimates(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			filteredEstimates.setAll(estimates);
		} else {
			String lower = keyword.toLowerCase();
			filteredEstimates.setAll(estimates.stream().filter(e -> {
				String name = Optional.ofNullable(customerNamesCache.get(e.getCustomerId())).orElse("");
				String job = Optional.ofNullable(e.getJobDescription()).orElse("");
				String total = e.getTotal() != null ? e.getTotal().toPlainString() : "";

				return name.toLowerCase().contains(lower) || job.toLowerCase().contains(lower)
						|| total.contains(lower.replaceAll("[^0-9.]", ""));
			}).collect(Collectors.toList()));
		}
	}

	@FXML
	private void handleNew() {
		ViewNavigator.loadView("EstimateFormView.fxml");
	}

	@FXML
	private void handleExport() {
		Estimate selected = estimateTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			new Alert(Alert.AlertType.INFORMATION, "Please select an estimate to export.").showAndWait();
			return;
		}

		EstimateExporter.exportToPdf(selected);
	}

	@FXML
	private void handleSendEmail() {
		Estimate selected = estimateTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			new Alert(Alert.AlertType.INFORMATION, "Please select an estimate to send.").showAndWait();
			return;
		}

		try {
			File pdf = EstimateExporter.generatePdfTemp(selected);

			Company customer = CompanyDAO.findById(selected.getCustomerId());
			String customerEmail = (customer != null && customer.getEmail() != null) ? customer.getEmail() : "";

			ViewNavigator.openDialog("EmailDialog.fxml", controller -> {
				controller.setData(customerEmail, List.of(pdf));
			});

		} catch (Exception e) {
			e.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Error opening email dialog: " + e.getMessage()).showAndWait();
		}
	}

	private void handleEdit(Estimate estimate) {
		ViewNavigator.loadView("EstimateFormView.fxml", estimate);
	}

	private void handleDelete(Estimate estimate) {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Delete Confirmation");
		confirmation.setHeaderText("Are you sure you want to delete this estimate?");
		confirmation.setContentText("This action cannot be undone.");

		ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
		confirmation.getButtonTypes().setAll(yesBtn, noBtn);

		confirmation.showAndWait().ifPresent(response -> {
			if (response == yesBtn) {
				boolean success = EstimateDAO.delete(estimate.getId());
				if (success) {
					deleteEstimateFiles(estimate);

					estimates.remove(estimate);
					filteredEstimates.remove(estimate);
				} else {
					new Alert(Alert.AlertType.ERROR, "Failed to delete estimate.").showAndWait();
				}
			}
		});
	}

	private void deleteEstimateFiles(Estimate estimate) {
		if (estimate == null || estimate.getDate() == null || estimate.getCustomerId() == null
				|| estimate.getCompanyId() == null)
			return;

		Company customer = CompanyDAO.findById(estimate.getCustomerId());
		if (customer == null)
			return;

		String folderName = estimate.getDate().toString();
		String customerName = customer.getName().replaceAll("[^a-zA-Z0-9_\\-]", "_");
		String contractFolderName = "contract_" + estimate.getId();

		// Carpeta completa: /estimates/YYYY-MM-DD/customer_name/contract_ID/
		File contractFolder = new File(System.getProperty("user.dir") + File.separator + "estimates" + File.separator
				+ folderName + File.separator + customerName + File.separator + contractFolderName);

		// Eliminar imágenes
		List<String> imageNames = estimate.getImageNames();
		if (imageNames != null) {
			for (String name : imageNames) {
				File file = new File(contractFolder, name);
				if (file.exists()) {
					boolean deleted = file.delete();
					System.out.println("Deleted image: " + file.getAbsolutePath() + " => " + deleted);
				}
			}
		}

		// Eliminar carpeta de contractor si queda vacía
		if (contractFolder.exists() && contractFolder.isDirectory() && contractFolder.list().length == 0) {
			boolean deleted = contractFolder.delete();
			System.out.println("Deleted contract folder: " + contractFolder.getAbsolutePath() + " => " + deleted);
		}

		// Eliminar carpeta de cliente si queda vacía
		File customerFolder = contractFolder.getParentFile();
		if (customerFolder != null && customerFolder.isDirectory() && customerFolder.list().length == 0) {
			boolean deleted = customerFolder.delete();
			System.out.println("Deleted customer folder: " + customerFolder.getAbsolutePath() + " => " + deleted);
		}

		// Eliminar carpeta de fecha si queda vacía
		File dateFolder = customerFolder != null ? customerFolder.getParentFile() : null;
		if (dateFolder != null && dateFolder.isDirectory() && dateFolder.list().length == 0) {
			boolean deleted = dateFolder.delete();
			System.out.println("Deleted date folder: " + dateFolder.getAbsolutePath() + " => " + deleted);
		}
	}

}