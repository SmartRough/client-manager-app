
package com.smartrough.app.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Contract;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ContractExporter {

	public static void exportToPdf(Contract contract) {
		try {
			Company company = CompanyDAO.findOwnCompany();
			String html = ContractHtmlTemplate.generateHtml(contract, company);

			File file = FileSaveHelper.showSaveDialog(null, "Contract_" + contract.getId() + ".pdf", "PDF Documents",
					"*.pdf");
			if (file == null)
				return;

			// 1. Crear el contrato como PDF temporal
			File basePdf = PdfGenerator.renderHtmlToTempPdf(html);

			// 2. Obtener los anexos PDF reales
			List<File> attachments = getPdfAttachments(contract);

			// 3. Unir contrato + anexos PDF directamente sin convertirlos en imágenes
			PdfMerger.mergePdfFiles(basePdf, attachments, file);

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

	private static List<File> getPdfAttachments(Contract contract) {
		List<File> pdfs = new java.util.ArrayList<>();

		if (contract.getAttachments() != null) {
			int index = 0;
			for (var att : contract.getAttachments()) {
				String ext = att.getExtension().toLowerCase();
				String folder = contract.getMeasureDate() != null ? contract.getMeasureDate().toString() : "unknown";
				String poSafe = contract.getPoNumber() != null ? contract.getPoNumber().replaceAll("[^a-zA-Z0-9]", "_")
						: "no_po";
				File file = new File(System.getProperty("user.dir"),
						"contracts/" + folder + "/" + poSafe + "/" + att.getFullFilename());

				if (!file.exists())
					continue;

				try {
					if ("pdf".equals(ext)) {
						pdfs.add(file);
					} else if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
						char annexLetter = (char) ('A' + index);
						String title = "Annex " + annexLetter + ": " + att.getFullFilename();
						File annexPdf = ImageToAnnexPdfConverter.createAnnexPdf(file, title);
						pdfs.add(annexPdf);
						index++;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return pdfs;
	}

	private static void showSuccess(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Export Successful");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}
