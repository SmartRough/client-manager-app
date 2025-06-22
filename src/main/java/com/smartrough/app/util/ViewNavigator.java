package com.smartrough.app.util;

import java.util.function.Consumer;

import com.smartrough.app.controller.CompanyFormController;
import com.smartrough.app.controller.ContractFormController;
import com.smartrough.app.controller.EmailDialogController;
import com.smartrough.app.controller.EstimateFormController;
import com.smartrough.app.controller.InvoiceFormController;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Contract;
import com.smartrough.app.model.Estimate;
import com.smartrough.app.model.Invoice;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewNavigator {

	private static BorderPane mainLayout;

	public static void setMainLayout(BorderPane layout) {
		mainLayout = layout;
	}

	public static void loadView(String fxml) {
		try {
			FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/smartrough/app/" + fxml));
			Parent view = loader.load();
			mainLayout.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadView(String fxml, Company company) {
		try {
			FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/smartrough/app/" + fxml));
			Parent view = loader.load();
			CompanyFormController controller = loader.getController();
			controller.loadCompany(company);
			mainLayout.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadView(String fxml, Invoice invoice) {
		try {
			FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/smartrough/app/" + fxml));
			Parent view = loader.load();
			InvoiceFormController controller = loader.getController();
			controller.loadInvoice(invoice);
			mainLayout.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadView(String fxml, Estimate estimate) {
		try {
			FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/smartrough/app/" + fxml));
			Parent view = loader.load();
			EstimateFormController controller = loader.getController();
			controller.loadEstimate(estimate);
			mainLayout.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadView(String fxml, Contract contract) {
		try {
			FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/smartrough/app/view/" + fxml));
			Parent view = loader.load();
			ContractFormController controller = loader.getController();
			controller.loadContract(contract);
			mainLayout.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openDialog(String fxml, Consumer<EmailDialogController> configurer) {
		try {
			FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/smartrough/app/" + fxml));
			Parent view = loader.load();
			EmailDialogController controller = loader.getController();
			if (configurer != null)
				configurer.accept(controller);

			Stage stage = new Stage();
			stage.setTitle("Send Email");
			stage.setScene(new Scene(view));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
