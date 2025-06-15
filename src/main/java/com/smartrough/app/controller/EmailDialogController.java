package com.smartrough.app.controller;

import com.smartrough.app.service.EmailService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class EmailDialogController {

	@FXML
	private TextField toField;

	@FXML
	private TextField subjectField;

	@FXML
	private TextArea bodyArea;

	@FXML
	private ListView<String> attachmentList;

	@FXML
	private Label statusLabel;

	private List<File> attachments;

	public void setData(String to, List<File> attachments) {
		this.toField.setText(to);
		this.attachments = attachments;
		this.attachmentList.getItems().addAll(attachments.stream().map(File::getName).toList());
	}

	@FXML
	private void handleSend() {
		String to = toField.getText().trim();
		String subject = subjectField.getText().trim();
		String body = bodyArea.getText();

		if (to.isEmpty() || subject.isEmpty()) {
			statusLabel.setText("Recipient and subject are required.");
			return;
		}

		try {
			EmailService.sendEmail(to, subject, body, attachments);
			statusLabel.setText("Email sent successfully.");
			closeDialog();
		} catch (Exception e) {
			e.printStackTrace();
			statusLabel.setText("Error sending email: " + e.getMessage());
		}
	}

	@FXML
	private void handleCancel() {
		closeDialog();
	}

	private void closeDialog() {
		Stage stage = (Stage) toField.getScene().getWindow();
		stage.close();
	}
}
