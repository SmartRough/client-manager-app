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
	private TextField licenseField;
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
			licenseField.setText(currentCompany.getLicense());
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
		if (!isFormValid()) {
			return;
		}

		if (currentCompany == null) {
			currentCompany = new Company();
			currentCompany.setOwnCompany(true);
			currentCompany.setType(CompanyType.BUSINESS);
		}

		currentCompany.setName(nameField.getText());
		currentCompany.setRepresentative(repField.getText());
		currentCompany.setLicense(licenseField.getText());
		currentCompany.setPhone(phoneField.getText());
		currentCompany.setEmail(emailField.getText());

		// Dirección
		Address address;

		if (currentCompany.getAddressId() > 0) {
			address = AddressDAO.findById(currentCompany.getAddressId());
			if (address == null) {
				address = new Address();
			}
		} else {
			address = new Address();
		}

		address.setStreet(streetField.getText());
		address.setCity(cityField.getText());
		address.setState(stateField.getText());
		address.setZipCode(zipField.getText());

		if (address.getId() > 0) {
			AddressDAO.update(address);
		} else {
			Address saved = AddressDAO.save(address);
			currentCompany.setAddressId(saved.getId());
		}

		if (currentCompany.getId() > 0) {
			CompanyDAO.updateCompany(currentCompany);
		} else {
			CompanyDAO.saveCompany(currentCompany);
		}

		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Company information saved successfully.");
		alert.showAndWait();
	}

	private boolean isFormValid() {
		if (isEmpty(nameField) || isEmpty(repField) || isEmpty(licenseField) || isEmpty(phoneField)
				|| isEmpty(emailField) || isEmpty(streetField) || isEmpty(cityField) || isEmpty(stateField)
				|| isEmpty(zipField)) {

			Alert alert = new Alert(Alert.AlertType.WARNING, "All fields must be filled out.", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		return true;
	}

	private boolean isEmpty(TextField field) {
		return field.getText() == null || field.getText().trim().isEmpty();
	}

}
