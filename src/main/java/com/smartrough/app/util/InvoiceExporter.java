package com.smartrough.app.util;

import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import com.smartrough.app.dao.CompanyDAO;
import com.smartrough.app.dao.InvoiceItemDAO;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Invoice;
import com.smartrough.app.model.InvoiceItem;
import javafx.scene.control.Alert;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class InvoiceExporter {

	public static void exportToPdf(Invoice invoice) {
		try {
			Company company = CompanyDAO.findById(invoice.getCompanyId());
			Company customer = CompanyDAO.findById(invoice.getCustomerId());
			List<InvoiceItem> items = InvoiceItemDAO.findByInvoiceId(invoice.getId());

			File file = FileSaveHelper.showSaveDialog(null, "Invoice_" + invoice.getInvoiceNumber() + ".pdf",
					"PDF Documents", "*.pdf");

			if (file == null) {
				System.out.println("Export canceled by user.");
				return;
			}

			Document doc = new Document();
			PdfWriter.getInstance(doc, new FileOutputStream(file));
			doc.open();

			doc.add(new Paragraph("INVOICE #" + invoice.getInvoiceNumber()));
			doc.add(new Paragraph("Date: " + invoice.getDate()));
			doc.add(new Paragraph("From: " + company.getName()));
			doc.add(new Paragraph("To: " + customer.getName()));
			doc.add(new Paragraph("\nItems:"));

			PdfPTable table = new PdfPTable(2);
			table.addCell("Description");
			table.addCell("Amount");

			for (InvoiceItem item : items) {
				table.addCell(item.getDescription());
				table.addCell(item.getAmount().toString());
			}

			doc.add(table);
			doc.add(new Paragraph("\nSubtotal: " + invoice.getSubtotal()));
			doc.add(new Paragraph("Tax Rate: " + (invoice.getTaxRate() != null ? invoice.getTaxRate() : "N/A")));
			doc.add(new Paragraph("Additional Costs: "
					+ (invoice.getAdditionalCosts() != null ? invoice.getAdditionalCosts() : "N/A")));
			doc.add(new Paragraph("Total: " + invoice.getTotal()));
			doc.add(new Paragraph("\nNotes: " + invoice.getNotes()));

			doc.close();
			System.out.println("PDF exported to: " + file.getAbsolutePath());

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Export Successful");
			alert.setHeaderText(null);
			alert.setContentText("Invoice was exported to:\n" + file.getAbsolutePath());
			alert.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportToWord(Invoice invoice) {
		try {
			Company company = CompanyDAO.findById(invoice.getCompanyId());
			Company customer = CompanyDAO.findById(invoice.getCustomerId());
			List<InvoiceItem> items = InvoiceItemDAO.findByInvoiceId(invoice.getId());

			File file = FileSaveHelper.showSaveDialog(null, "Invoice_" + invoice.getInvoiceNumber() + ".docx",
					"Word Documents", "*.docx");

			if (file == null) {
				System.out.println("Export canceled by user.");
				return;
			}

			XWPFDocument doc = new XWPFDocument();
			FileOutputStream out = new FileOutputStream(file);

			XWPFParagraph title = doc.createParagraph();
			title.setAlignment(ParagraphAlignment.CENTER);
			XWPFRun run = title.createRun();
			run.setText("INVOICE #" + invoice.getInvoiceNumber());
			run.setBold(true);
			run.setFontSize(16);

			XWPFParagraph info = doc.createParagraph();
			info.setSpacingAfter(200);
			XWPFRun r1 = info.createRun();
			r1.setText("Date: " + invoice.getDate());
			r1.addBreak();
			r1.setText("From: " + company.getName());
			r1.addBreak();
			r1.setText("To: " + customer.getName());

			XWPFTable table = doc.createTable();
			XWPFTableRow header = table.getRow(0);
			header.getCell(0).setText("Description");
			header.addNewTableCell().setText("Amount");

			for (InvoiceItem item : items) {
				XWPFTableRow row = table.createRow();
				row.getCell(0).setText(item.getDescription());
				row.getCell(1).setText(item.getAmount().toString());
			}

			XWPFParagraph totals = doc.createParagraph();
			totals.setSpacingBefore(400);
			XWPFRun r2 = totals.createRun();
			r2.addBreak();
			r2.setText("Subtotal: " + invoice.getSubtotal());
			r2.addBreak();
			r2.setText("Tax Rate: " + (invoice.getTaxRate() != null ? invoice.getTaxRate() : "N/A"));
			r2.addBreak();
			r2.setText("Additional Costs: "
					+ (invoice.getAdditionalCosts() != null ? invoice.getAdditionalCosts() : "N/A"));
			r2.addBreak();
			r2.setText("Total: " + invoice.getTotal());
			r2.addBreak();
			r2.setText("Notes: " + invoice.getNotes());

			doc.write(out);
			out.close();

			System.out.println("Word document exported to: " + file.getAbsolutePath());

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Export Successful");
			alert.setHeaderText(null);
			alert.setContentText("Invoice was exported to:\n" + file.getAbsolutePath());
			alert.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
