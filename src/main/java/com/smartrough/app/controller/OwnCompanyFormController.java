package com.smartrough.app.controller;

import com.smartrough.app.dao.AddressDAO;
import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.enums.CompanyType;
import com.smartrough.app.model.Address;
import com.smartrough.app.model.Company;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class OwnCompanyFormController {

	@FXML
	private TextField nameField;
	@FXML
	private TextField repField;
	@FXML
	private TextField phoneField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField streetField;
	@FXML
	private TextField cityField;
	@FXML
	private TextField stateField;
	@FXML
	private TextField zipField;

	private Company currentCompany;

	@FXML
	public void initialize() {
		currentCompany = CompanyDAO.findOwnCompany();
		if (currentCompany != null) {
			nameField.setText(currentCompany.getName());
			repField.setText(currentCompany.getRepresentative());
			phoneField.setText(currentCompany.getPhone());
			emailField.setText(currentCompany.getEmail());

			Address address = AddressDAO.findById(currentCompany.getAddressId());
			if (address != null) {
				streetField.setText(address.getStreet());
				cityField.setText(address.getCity());
				stateField.setText(address.getState());
				zipField.setText(address.getZipCode());
			}
		}
	}

	@FXML
	private void handleSave() {
		if (currentCompany == null) {
			currentCompany = new Company();
			currentCompany.setOwnCompany(true);
			currentCompany.setType(CompanyType.BUSINESS);
		}

		currentCompany.setName(nameField.getText());
		currentCompany.setRepresentative(repField.getText());
		currentCompany.setPhone(phoneField.getText());
		currentCompany.setEmail(emailField.getText());

		// Dirección
		Address address = new Address();
		address.setStreet(streetField.getText());
		address.setCity(cityField.getText());
		address.setState(stateField.getText());
		address.setZipCode(zipField.getText());

		Address savedAddr = AddressDAO.save(address);
		currentCompany.setAddressId(savedAddr.getId());

		if (currentCompany.getId() > 0) {
			CompanyDAO.updateCompany(currentCompany);
		} else {
			CompanyDAO.saveCompany(currentCompany);
		}

		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Company information saved successfully.");
		alert.showAndWait();
	}

	@FXML
	private void handleCancel() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "You can return using the left menu.");
		alert.showAndWait();
	}
}
