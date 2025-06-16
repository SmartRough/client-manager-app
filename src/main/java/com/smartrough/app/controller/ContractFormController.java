package com.smartrough.app.controller;

import com.smartrough.app.dao.ContractDAO;
import com.smartrough.app.model.Contract;
import com.smartrough.app.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ContractFormController {

	private Contract contract;

	// Sección: Basic Info
	@FXML
	private TextField poField;
	@FXML
	private DatePicker measureDatePicker, datePicker;

	// Sección: Owner & Contact
	@FXML
	private TextField owner1Field, owner2Field, addressField, cityField, stateField, zipField;
	@FXML
	private TextField emailField, homePhoneField, otherPhoneField;

	// Sección: Property Type
	@FXML
	private CheckBox houseCheck, condoCheck, mfhCheck, commercialCheck, hoaCheck;

	// Sección: Financials
	@FXML
	private TextField totalField, depositField, balanceField, financedField;

	// Sección: Card Info
	@FXML
	private TextField cardTypeField, cardNumberField, cardZipField, cardCVCField, cardExpField;

	/**
	 * Cargar datos en el formulario.
	 */
	public void loadContract(Contract existingContract) {
		this.contract = existingContract != null ? existingContract : new Contract();

		if (existingContract != null) {
			// Basic Info
			poField.setText(contract.getPoNumber());
			measureDatePicker.setValue(contract.getMeasureDate());
			datePicker.setValue(contract.getDate());

			// Contact
			owner1Field.setText(contract.getOwner1());
			owner2Field.setText(contract.getOwner2());
			addressField.setText(contract.getAddress());
			cityField.setText(contract.getCity());
			stateField.setText(contract.getState());
			zipField.setText(contract.getZip());
			emailField.setText(contract.getEmail());
			homePhoneField.setText(contract.getHomePhone());
			otherPhoneField.setText(contract.getOtherPhone());

			// Property
			houseCheck.setSelected(contract.isHouse());
			condoCheck.setSelected(contract.isCondo());
			mfhCheck.setSelected(contract.isMFH());
			commercialCheck.setSelected(contract.isCommercial());
			hoaCheck.setSelected(contract.isHasHOA());

			// Financials
			totalField.setText(toString(contract.getTotalPrice()));
			depositField.setText(toString(contract.getDeposit()));
			balanceField.setText(toString(contract.getBalanceDue()));
			financedField.setText(toString(contract.getAmountFinanced()));

			// Card Info
			cardTypeField.setText(contract.getCardType());
			cardNumberField.setText(contract.getCardNumber());
			cardZipField.setText(contract.getCardZip());
			cardCVCField.setText(contract.getCardCVC());
			cardExpField.setText(contract.getCardExp());
		}
	}

	@FXML
	private void handleSave() {
		// Basic Info
		contract.setPoNumber(poField.getText());
		contract.setMeasureDate(measureDatePicker.getValue());
		contract.setDate(datePicker.getValue());

		// Contact
		contract.setOwner1(owner1Field.getText());
		contract.setOwner2(owner2Field.getText());
		contract.setAddress(addressField.getText());
		contract.setCity(cityField.getText());
		contract.setState(stateField.getText());
		contract.setZip(zipField.getText());
		contract.setEmail(emailField.getText());
		contract.setHomePhone(homePhoneField.getText());
		contract.setOtherPhone(otherPhoneField.getText());

		// Property
		contract.setHouse(houseCheck.isSelected());
		contract.setCondo(condoCheck.isSelected());
		contract.setMFH(mfhCheck.isSelected());
		contract.setCommercial(commercialCheck.isSelected());
		contract.setHasHOA(hoaCheck.isSelected());

		// Financials
		contract.setTotalPrice(parseDoubleOrZero(totalField.getText()));
		contract.setDeposit(parseDoubleOrZero(depositField.getText()));
		contract.setBalanceDue(parseDoubleOrZero(balanceField.getText()));
		contract.setAmountFinanced(parseDoubleOrZero(financedField.getText()));

		// Card Info
		contract.setCardType(cardTypeField.getText());
		contract.setCardNumber(cardNumberField.getText());
		contract.setCardZip(cardZipField.getText());
		contract.setCardCVC(cardCVCField.getText());
		contract.setCardExp(cardExpField.getText());

		if (contract.getId() != null) {
			ContractDAO.update(contract);
		} else {
			ContractDAO.save(contract);
		}

		ViewNavigator.loadView("ContractListView.fxml"); // Regresar a la lista al guardar
	}

	@FXML
	private void handleCancel() {
		ViewNavigator.loadView("ContractListView.fxml");
	}

	private double parseDoubleOrZero(String text) {
		try {
			return text != null && !text.isBlank() ? Double.parseDouble(text) : 0.0;
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	private String toString(Double value) {
		return value != null ? String.valueOf(value) : "";
	}
}
