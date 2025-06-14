package com.smartrough.app.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.smartrough.app.dao.AddressDAO;
import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.InvoiceItemDAO;
import com.smartrough.app.model.Address;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Invoice;
import com.smartrough.app.model.InvoiceItem;
import javafx.scene.control.Alert;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class InvoiceExporter {

	public static void exportToPdf(Invoice invoice) {
		try {
			Company company = CompanyDAO.findById(invoice.getCompanyId());
			Company customer = CompanyDAO.findById(invoice.getCustomerId());
			Address companyAddress = AddressDAO.findById(company.getAddressId());
			Address customerAddress = AddressDAO.findById(customer.getAddressId());
			List<InvoiceItem> items = InvoiceItemDAO.findByInvoiceId(invoice.getId());

			String html = InvoiceHtmlTemplate.generateHtml(invoice, company, companyAddress, customer, customerAddress,
					items);

			File file = FileSaveHelper.showSaveDialog(null, "Invoice_" + invoice.getInvoiceNumber() + ".pdf",
					"PDF Documents", "*.pdf");

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

	public static void exportToWord(Invoice invoice) {
		try {
			Company company = CompanyDAO.findById(invoice.getCompanyId());
			Company customer = CompanyDAO.findById(invoice.getCustomerId());
			Address companyAddress = AddressDAO.findById(company.getAddressId());
			Address customerAddress = AddressDAO.findById(customer.getAddressId());
			List<InvoiceItem> items = InvoiceItemDAO.findByInvoiceId(invoice.getId());

			String html = InvoiceHtmlTemplate.generateHtml(invoice, company, companyAddress, customer, customerAddress,
					items);

			File file = FileSaveHelper.showSaveDialog(null, "Invoice_" + invoice.getInvoiceNumber() + ".docx",
					"Word Documents", "*.docx");

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

	private static void showSuccess(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Export Successful");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}
