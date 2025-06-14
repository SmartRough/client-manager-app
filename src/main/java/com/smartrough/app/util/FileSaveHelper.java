package com.smartrough.app.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

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

	public static String encodeImageToBase64(String path) {
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(path));
			return Base64.getEncoder().encodeToString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
