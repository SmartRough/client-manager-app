package com.smartrough.app.controller;

import com.smartrough.app.dao.CRUDHelper;
import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Address;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Types;

public class CompanyFormController {

	@FXML
	private TextField nameField, repField, phoneField, emailField;
	@FXML
	private CheckBox ownCompanyCheck;
	@FXML
	private TextField streetField, cityField, stateField, zipField;

	@FXML
	private void handleSave() {
		Address address = new Address();
		address.setStreet(streetField.getText());
		address.setCity(cityField.getText());
		address.setState(stateField.getText());
		address.setZipCode(zipField.getText());

		// Guardar dirección primero
		String[] cols = { "street", "city", "state", "zip_code" };
		Object[] vals = { address.getStreet(), address.getCity(), address.getState(), address.getZipCode() };
		int[] types = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
		long addressId = CRUDHelper.create("Address", cols, vals, types);

		// Crear y guardar empresa
		Company company = new Company();
		company.setName(nameField.getText());
		company.setRepresentative(repField.getText());
		company.setPhone(phoneField.getText());
		company.setEmail(emailField.getText());
		company.setAddressId(addressId);
		company.setOwnCompany(ownCompanyCheck.isSelected());

		long companyId = CompanyDAO.saveCompany(company);
		if (companyId > 0) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Company saved successfully", ButtonType.OK);
			alert.showAndWait();
			clearFields();
		}
	}

	private void clearFields() {
		nameField.clear();
		repField.clear();
		phoneField.clear();
		emailField.clear();
		streetField.clear();
		cityField.clear();
		stateField.clear();
		zipField.clear();
		ownCompanyCheck.setSelected(false);
	}
}
