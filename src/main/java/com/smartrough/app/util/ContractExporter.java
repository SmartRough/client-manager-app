
package com.smartrough.app.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Contract;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileOutputStream;

public class ContractExporter {

	public static void exportToPdf(Contract contract) {
		try {
			Company company = CompanyDAO.findOwnCompany();
			String html = ContractHtmlTemplate.generateHtml(contract, company);

			File file = FileSaveHelper.showSaveDialog(null, "Contract_" + contract.getId() + ".pdf", "PDF Documents",
					"*.pdf");
			if (file == null)
				return;

			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.withHtmlContent(html, null);
			builder.toStream(new FileOutputStream(file));
			builder.run();

			showSuccess("PDF exported to: " + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File generatePdfTemp(Contract contract) throws Exception {
		Company company = CompanyDAO.findOwnCompany();
		String html = ContractHtmlTemplate.generateHtml(contract, company);

		File tempFile = File.createTempFile("Contract_" + contract.getId(), ".pdf");
		tempFile.deleteOnExit();

		PdfRendererBuilder builder = new PdfRendererBuilder();
		builder.withHtmlContent(html, null);
		builder.toStream(new FileOutputStream(tempFile));
		builder.run();

		return tempFile;
	}

	private static void showSuccess(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Export Successful");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}
