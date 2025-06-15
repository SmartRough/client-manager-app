package com.smartrough.app.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Estimate;

import javafx.scene.control.Alert;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.io.FileOutputStream;

public class EstimateExporter {

	public static void exportToPdf(Estimate estimate) {
		try {
			Company company = CompanyDAO.findById(estimate.getCompanyId());
			Company customer = CompanyDAO.findById(estimate.getCustomerId());

			String html = EstimateHtmlTemplate.generateHtml(estimate, company, customer);

			File file = FileSaveHelper.showSaveDialog(null, "Estimate_" + estimate.getId() + ".pdf", "PDF Documents",
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

	public static void exportToWord(Estimate estimate) {
		try {
			Company company = CompanyDAO.findById(estimate.getCompanyId());
			Company customer = CompanyDAO.findById(estimate.getCustomerId());

			String html = EstimateHtmlTemplate.generateHtml(estimate, company, customer);

			File file = FileSaveHelper.showSaveDialog(null, "Estimate_" + estimate.getId() + ".docx", "Word Documents",
					"*.docx");

			if (file == null)
				return;

			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
			XHTMLImporterImpl importer = new XHTMLImporterImpl(wordMLPackage);
			wordMLPackage.getMainDocumentPart().getContent().addAll(importer.convert(html, null));
			wordMLPackage.save(file);

			showSuccess("Word document exported to: " + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File generatePdfTemp(Estimate estimate) throws Exception {
		Company company = CompanyDAO.findById(estimate.getCompanyId());
		Company customer = CompanyDAO.findById(estimate.getCustomerId());

		String html = EstimateHtmlTemplate.generateHtml(estimate, company, customer);

		File tempFile = File.createTempFile("Estimate_" + estimate.getId(), ".pdf");
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
