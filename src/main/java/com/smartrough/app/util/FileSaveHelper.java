package com.smartrough.app.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class FileSaveHelper {

	public static File showSaveDialog(Window parentWindow, String suggestedFileName, String extensionDescription,
			String extensionPattern) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		fileChooser.setInitialFileName(suggestedFileName);
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionDescription, extensionPattern));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		return fileChooser.showSaveDialog(parentWindow);
	}
}
