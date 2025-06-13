package com.smartrough.app.controller;

import com.smartrough.app.dao.CRUDHelper;
import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.model.Address;
import com.smartrough.app.model.Company;
import com.smartrough.app.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.Types;
import java.util.List;

public class CompanyFormController {

	@FXML
	private TextField nameField, repField, phoneField, emailField;
	@FXML
	private CheckBox ownCompanyCheck;
	@FXML
	private TextField streetField, cityField, stateField, zipField;
	@FXML
	private ComboBox<Address> addressComboBox;
	@FXML
	private GridPane addressGrid;
	@FXML
	private Button addAddressButton;

	private boolean creatingNewAddress = false;

	private Company companyBeingEdited;

	@FXML
	public void initialize() {
		List<Address> addresses = CRUDHelper.findAllAddresses();
		addressGrid.setVisible(false);
		addressGrid.setManaged(false);
		addressComboBox.setItems(FXCollections.observableArrayList(addresses));
		addressComboBox.setPromptText("Select or create an address...");

		// Muestra calle y ciudad como preview
		addressComboBox.setCellFactory(cb -> new ListCell<>() {
			@Override
			protected void updateItem(Address addr, boolean empty) {
				super.updateItem(addr, empty);
				if (empty || addr == null) {
					setText(null);
				} else {
					setText(addr.getStreet() + ", " + addr.getCity());
				}
			}
		});
		addressComboBox.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Address addr, boolean empty) {
				super.updateItem(addr, empty);
				if (empty || addr == null) {
					setText("Select or create an address...");
				} else {
					setText(addr.getStreet() + ", " + addr.getCity());
				}
			}
		});

		addressComboBox.setOnAction(e -> {
			Address selected = addressComboBox.getSelectionModel().getSelectedItem();
			if (selected != null) {
				streetField.setText(selected.getStreet());
				cityField.setText(selected.getCity());
				stateField.setText(selected.getState());
				zipField.setText(selected.getZipCode());
			} else {
				clearAddressFields();
			}
		});
	}

	@FXML
	private void handleSave() {
		long addressId;

		if (creatingNewAddress) {
			Address address = new Address();
			address.setStreet(streetField.getText());
			address.setCity(cityField.getText());
			address.setState(stateField.getText());
			address.setZipCode(zipField.getText());

			String[] cols = { "street", "city", "state", "zip_code" };
			Object[] vals = { address.getStreet(), address.getCity(), address.getState(), address.getZipCode() };
			int[] types = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
			addressId = CRUDHelper.create("Address", cols, vals, types);
		} else {
			Address selected = addressComboBox.getSelectionModel().getSelectedItem();
			addressId = selected != null ? selected.getId() : -1;
		}

		if (companyBeingEdited == null) {
			Company company = new Company();
			company.setName(nameField.getText());
			company.setRepresentative(repField.getText());
			company.setPhone(phoneField.getText());
			company.setEmail(emailField.getText());
			company.setAddressId(addressId);
			company.setOwnCompany(ownCompanyCheck.isSelected());

			long companyId = CompanyDAO.saveCompany(company);
			if (companyId > 0) {
				showAlert("Company saved successfully");
				clearFields();
			}
		} else {
			companyBeingEdited.setName(nameField.getText());
			companyBeingEdited.setRepresentative(repField.getText());
			companyBeingEdited.setPhone(phoneField.getText());
			companyBeingEdited.setEmail(emailField.getText());
			companyBeingEdited.setOwnCompany(ownCompanyCheck.isSelected());
			companyBeingEdited.setAddressId(addressId);

			CompanyDAO.updateCompany(companyBeingEdited);
			showAlert("Company updated successfully");
		}
	}

	@FXML
	private void handleCancel() {
		ViewNavigator.loadView("CompanyListView.fxml");
	}

	@FXML
	private void handleToggleAddressForm() {
		creatingNewAddress = !creatingNewAddress;
		addressGrid.setVisible(creatingNewAddress);
		addressGrid.setManaged(creatingNewAddress);
		addressComboBox.setDisable(creatingNewAddress);

		if (creatingNewAddress) {
			clearAddressFields();
			addressComboBox.getSelectionModel().clearSelection();
		}
	}

	public void loadCompany(Company company) {
		this.companyBeingEdited = company;
		nameField.setText(company.getName());
		repField.setText(company.getRepresentative());
		phoneField.setText(company.getPhone());
		emailField.setText(company.getEmail());
		ownCompanyCheck.setSelected(company.isOwnCompany());

		// Buscar dirección por ID para preseleccionar en el ComboBox
		for (Address addr : addressComboBox.getItems()) {
			if (addr != null && addr.getId() == company.getAddressId()) {
				addressComboBox.getSelectionModel().select(addr);
				streetField.setText(addr.getStreet());
				cityField.setText(addr.getCity());
				stateField.setText(addr.getState());
				zipField.setText(addr.getZipCode());
				break;
			}
		}
	}

	private void clearFields() {
		nameField.clear();
		repField.clear();
		phoneField.clear();
		emailField.clear();
		ownCompanyCheck.setSelected(false);
		addressComboBox.getSelectionModel().clearSelection();
		clearAddressFields();
	}

	private void clearAddressFields() {
		streetField.clear();
		cityField.clear();
		stateField.clear();
		zipField.clear();
	}

	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
		alert.showAndWait();
	}
}
