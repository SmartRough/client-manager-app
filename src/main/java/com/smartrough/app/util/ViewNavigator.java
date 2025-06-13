package com.smartrough.app.util;

import com.smartrough.app.controller.CompanyFormController;
import com.smartrough.app.model.Company;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class ViewNavigator {

	private static BorderPane mainLayout;

	public static void setMainLayout(BorderPane layout) {
		mainLayout = layout;
	}

	public static void loadView(String fxml) {
		try {
			FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/smartrough/app/view/" + fxml));
			Parent view = loader.load();
			mainLayout.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadView(String fxml, Company company) {
		try {
			FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/smartrough/app/view/" + fxml));
			Parent view = loader.load();
			CompanyFormController controller = loader.getController();
			controller.loadCompany(company);
			mainLayout.setCenter(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
