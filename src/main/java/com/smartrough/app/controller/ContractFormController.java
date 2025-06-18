package com.smartrough.app.controller;

import com.smartrough.app.dao.ContractDAO;
import com.smartrough.app.model.Contract;
import com.smartrough.app.model.ContractAttachment;
import com.smartrough.app.model.ContractItem;
import com.smartrough.app.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContractFormController {

	@FXML
	private TextField poNumberField;
	@FXML
	private DatePicker measureDatePicker;
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private TextField owner1Field;
	@FXML
	private TextField owner2Field;
	@FXML
	private TextField addressField;
	@FXML
	private TextField cityField;
	@FXML
	private TextField stateField;
	@FXML
	private TextField zipField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField homePhoneField;
	@FXML
	private TextField otherPhoneField;

	@FXML
	private RadioButton houseCheck;
	@FXML
	private RadioButton condoCheck;
	@FXML
	private RadioButton mfhCheck;
	@FXML
	private RadioButton commercialCheck;
	@FXML
	private ToggleGroup propertyTypeGroup;

	@FXML
	private CheckBox hoaCheck;

	@FXML
	private TextField totalPriceField;
	@FXML
	private TextField depositField;
	@FXML
	private TextField balanceField;
	@FXML
	private TextField amountFinancedField;
	@FXML
	private ComboBox<String> cardTypeField;
	@FXML
	private TextField cardNumberField;
	@FXML
	private TextField cardZipField;
	@FXML
	private TextField cardCvcField;
	@FXML
	private TextField cardExpField;

	@FXML
	private TableView<ContractItem> itemTable;
	@FXML
	private TableColumn<ContractItem, String> itemDescriptionCol;
	@FXML
	private TableColumn<ContractItem, Void> itemActionCol;
	@FXML
	private TextField newItemField;

	@FXML
	private TableView<ContractAttachment> attachmentTable;
	@FXML
	private TableColumn<ContractAttachment, String> attachmentNameColumn;
	@FXML
	private TableColumn<ContractAttachment, Void> attachmentActionColumn;
	@FXML
	private Label attachmentLabel;

	private final ObservableList<ContractItem> items = FXCollections.observableArrayList();
	private final List<ContractAttachment> attachments = new ArrayList<>();
	private final List<String> newAttachmentNames = new ArrayList<>();
	private final List<File> filesToAdd = new ArrayList<>();
	private Contract contractBeingEdited;
	private boolean editing = false;

	@FXML
	public void initialize() {
		itemDescriptionCol.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
		itemActionCol.setCellFactory(col -> createDeleteButton(itemTable, items));
		itemTable.setItems(items);

		cardTypeField.getItems().addAll("Debit", "Credit");
		updateAttachmentTable();
	}

	private <T> TableCell<T, Void> createDeleteButton(TableView<T> table, ObservableList<T> list) {
		return new TableCell<>() {
			private final Button deleteButton = new Button();

			{
				ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/img/delete.png")));
				icon.setFitHeight(16);
				icon.setFitWidth(16);
				deleteButton.setGraphic(icon);
				deleteButton.getStyleClass().add("icon-button");
				deleteButton.setOnAction(e -> {
					T item = table.getItems().get(getIndex());
					list.remove(item);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		};
	}

	@FXML
	private void handleAddItem() {
		String desc = newItemField.getText();
		if (desc.isBlank()) {
			showAlert("Item description is required.");
			return;
		}
		items.add(new ContractItem(null, null, desc));
		newItemField.clear();
	}

	@FXML
	private void handleBrowseAttachments() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select Attachments");
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files", "*.*"));
		List<File> selected = chooser.showOpenMultipleDialog(null);
		if (selected != null) {
			filesToAdd.addAll(selected);
			attachmentLabel.setText(filesToAdd.size() + " selected");
		}
	}

	@FXML
	private void handleAddAttachment() {
		if (filesToAdd.isEmpty())
			return;

		List<String> existingAttachmentNames = attachments.stream().map(att -> att.getName() + "." + att.getExtension())
				.toList();

		for (File file : filesToAdd) {
			String name = getFileNameWithoutExtension(file.getName());
			String ext = getFileExtension(file.getName());
			String fullName = name + "." + ext;

			if (!existingAttachmentNames.contains(fullName)) {
				attachments.add(new ContractAttachment(null, null, name, ext));
				newAttachmentNames.add(fullName);
			}
		}

		attachmentLabel.setText("0 selected");
		updateAttachmentTable();
	}

	private void updateAttachmentTable() {
		ObservableList<ContractAttachment> data = FXCollections.observableArrayList(attachments);
		attachmentTable.setItems(data);

		attachmentNameColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
				cd.getValue().getName() + "." + cd.getValue().getExtension()));

		attachmentActionColumn.setCellFactory(col -> new TableCell<>() {
			private final Button deleteButton = new Button("Delete");

			{
				deleteButton.setOnAction(e -> {
					ContractAttachment att = getTableView().getItems().get(getIndex());
					attachments.remove(att);
					updateAttachmentTable();
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : deleteButton);
			}
		});
	}

	private void removeObsoleteAttachments(List<ContractAttachment> oldAttachments,
			List<ContractAttachment> newAttachments) {
		if (measureDatePicker.getValue() == null || poNumberField.getText().isBlank())
			return;

		String folderName = measureDatePicker.getValue().toString();
		String poNumber = poNumberField.getText().replaceAll("[^a-zA-Z0-9_\\-]", "_");
		File folder = new File(System.getProperty("user.dir"), "contracts/" + folderName + "/" + poNumber);

		for (ContractAttachment oldAtt : oldAttachments) {
			boolean stillExists = newAttachments.stream().anyMatch(newAtt -> newAtt.getName().equals(oldAtt.getName())
					&& newAtt.getExtension().equals(oldAtt.getExtension()));

			if (!stillExists) {
				File fileToDelete = new File(folder, oldAtt.getName() + "." + oldAtt.getExtension());
				if (fileToDelete.exists()) {
					boolean deleted = fileToDelete.delete();
					System.out.println("Deleting " + fileToDelete.getAbsolutePath() + " => " + deleted);
				}
			}
		}
	}

	private void copyNewAttachments(Contract contract) {
		if (newAttachmentNames.isEmpty())
			return;

		String folderName = contract.getMeasureDate().toString();
		String poNumber = contract.getPoNumber().replaceAll("[^a-zA-Z0-9_\\-]", "_");
		File destFolder = new File(System.getProperty("user.dir"), "contracts/" + folderName + "/" + poNumber);
		if (!destFolder.exists())
			destFolder.mkdirs();

		for (File source : filesToAdd) {
			String fullName = source.getName();
			if (newAttachmentNames.contains(fullName)) {
				try {
					File destFile = new File(destFolder, fullName);
					System.out.println("Copying to: " + destFile.getAbsolutePath());
					java.nio.file.Files.copy(source.toPath(), destFile.toPath(),
							java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					e.printStackTrace();
					showAlert("Failed to save attachment: " + source.getName());
				}
			}
		}

		filesToAdd.clear();
		newAttachmentNames.clear();
	}

	public void loadContract(Contract contract) {
		this.contractBeingEdited = contract;
		this.editing = true;

		poNumberField.setText(contract.getPoNumber());
		measureDatePicker.setValue(contract.getMeasureDate());
		startDatePicker.setValue(contract.getStartDate());
		endDatePicker.setValue(contract.getEndDate());
		owner1Field.setText(contract.getOwner1());
		owner2Field.setText(contract.getOwner2());
		addressField.setText(contract.getAddress());
		cityField.setText(contract.getCity());
		stateField.setText(contract.getState());
		zipField.setText(contract.getZip());
		emailField.setText(contract.getEmail());
		homePhoneField.setText(contract.getHomePhone());
		otherPhoneField.setText(contract.getOtherPhone());

		houseCheck.setSelected(contract.isHouse());
		condoCheck.setSelected(contract.isCondo());
		mfhCheck.setSelected(contract.isMFH());
		commercialCheck.setSelected(contract.isCommercial());
		hoaCheck.setSelected(contract.isHasHOA());

		totalPriceField.setText(String.valueOf(contract.getTotalPrice()));
		depositField.setText(String.valueOf(contract.getDeposit()));
		balanceField.setText(String.valueOf(contract.getBalanceDue()));
		amountFinancedField.setText(String.valueOf(contract.getAmountFinanced()));

		cardTypeField.setValue(contract.getCardType());
		cardNumberField.setText(contract.getCardNumber());
		cardZipField.setText(contract.getCardZip());
		cardCvcField.setText(contract.getCardCVC());
		cardExpField.setText(contract.getCardExp());

		items.clear();
		items.addAll(contract.getItems());

		attachments.clear();
		attachments.addAll(contract.getAttachments());
		updateAttachmentTable();
	}

	@FXML
	private void handleSave() {
		if (!validateForm())
			return;

		List<ContractAttachment> oldAttachments = editing && contractBeingEdited != null
				? new ArrayList<>(contractBeingEdited.getAttachments())
				: new ArrayList<>();

		Contract c = editing ? contractBeingEdited : new Contract();
		c.setPoNumber(poNumberField.getText());
		c.setMeasureDate(measureDatePicker.getValue());
		c.setStartDate(startDatePicker.getValue());
		c.setEndDate(endDatePicker.getValue());
		c.setOwner1(owner1Field.getText());
		c.setOwner2(owner2Field.getText());
		c.setAddress(addressField.getText());
		c.setCity(cityField.getText());
		c.setState(stateField.getText());
		c.setZip(zipField.getText());
		c.setEmail(emailField.getText());
		c.setHomePhone(homePhoneField.getText());
		c.setOtherPhone(otherPhoneField.getText());
		c.setHouse(houseCheck.isSelected());
		c.setCondo(condoCheck.isSelected());
		c.setMFH(mfhCheck.isSelected());
		c.setCommercial(commercialCheck.isSelected());
		c.setHasHOA(hoaCheck.isSelected());
		c.setTotalPrice(Double.parseDouble(totalPriceField.getText()));
		c.setDeposit(Double.parseDouble(depositField.getText()));
		c.setBalanceDue(Double.parseDouble(balanceField.getText()));
		c.setAmountFinanced(Double.parseDouble(amountFinancedField.getText()));
		c.setCardType(cardTypeField.getValue());
		c.setCardNumber(cardNumberField.getText());
		c.setCardZip(cardZipField.getText());
		c.setCardCVC(cardCvcField.getText());
		c.setCardExp(cardExpField.getText());
		c.setItems(new ArrayList<>(items));
		c.setAttachments(new ArrayList<>(attachments));

		if (!editing) {
			long id = ContractDAO.save(c);
			c.setId(id);
		} else {
			removeObsoleteAttachments(oldAttachments, attachments);
			ContractDAO.update(c);
		}

		copyNewAttachments(c);
		handleCancel();
	}

	@FXML
	private void handleCancel() {
		cleanupTemporaryAttachments();
		ViewNavigator.loadView("ContractListView.fxml");
	}

	private boolean validateForm() {
		if (measureDatePicker.getValue() == null || startDatePicker.getValue() == null
				|| endDatePicker.getValue() == null || owner1Field.getText().isBlank()
				|| addressField.getText().isBlank()) {
			showAlert("Date, owner and address are required.");
			return false;
		}
		if (items.isEmpty()) {
			showAlert("At least one item is required.");
			return false;
		}
		return true;
	}

	private String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		return (dotIndex != -1) ? fileName.substring(dotIndex + 1) : "";
	}

	private String getFileNameWithoutExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		return (dotIndex != -1) ? fileName.substring(0, dotIndex) : fileName;
	}

	private void showAlert(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
	}

	private void cleanupTemporaryAttachments() {
		if (filesToAdd.isEmpty())
			return;

		for (File file : filesToAdd) {
			String name = getFileNameWithoutExtension(file.getName());
			String ext = getFileExtension(file.getName());

			if (measureDatePicker.getValue() != null && !poNumberField.getText().isBlank()) {
				String folderName = measureDatePicker.getValue().toString();
				String poNumber = poNumberField.getText().replaceAll("[^a-zA-Z0-9_\\-]", "_");
				File destFile = new File(System.getProperty("user.dir"),
						"contracts/" + folderName + "/" + poNumber + "/" + name + "." + ext);
				if (destFile.exists()) {
					destFile.delete();
				}
			}
		}

		filesToAdd.clear();
	}

}
