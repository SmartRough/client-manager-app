package com.smartrough.app.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

	public static String encodeImageToBase64(String resourcePath) {
		try (InputStream is = FileSaveHelper.class.getResourceAsStream(resourcePath)) {
			if (is == null) {
				throw new FileNotFoundException("Resource not found: " + resourcePath);
			}
			byte[] imageBytes = is.readAllBytes();
			return Base64.getEncoder().encodeToString(imageBytes);
		} catch (IOException e) {
			throw new RuntimeException("Error reading image: " + resourcePath, e);
		}
	}

	public static String encodeFileToBase64(String fullFilePath) {
		try (InputStream is = new FileInputStream(fullFilePath)) {
			byte[] imageBytes = is.readAllBytes();
			return Base64.getEncoder().encodeToString(imageBytes);
		} catch (IOException e) {
			System.err.println("Error loading image: " + fullFilePath);
			return null;
		}
	}

}
