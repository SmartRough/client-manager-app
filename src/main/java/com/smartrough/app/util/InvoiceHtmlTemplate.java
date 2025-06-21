package com.smartrough.app.util;

import com.smartrough.app.model.Address;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Invoice;
import com.smartrough.app.model.InvoiceItem;

import java.util.List;

public class InvoiceHtmlTemplate {

	public static String generateHtml(Invoice invoice, Company company, Address companyAddress, Company customer,
			Address customerAddress, List<InvoiceItem> items) {

		StringBuilder sb = new StringBuilder();
		String logoBase64 = FileSaveHelper.encodeImageToBase64("/img/logo_gcs.png");

		sb.append("<!DOCTYPE html>");
		sb.append("<html lang='en' xmlns='http://www.w3.org/1999/xhtml'>");
		sb.append("<head>");
		sb.append("<meta charset='UTF-8' />");
		sb.append("<title>Invoice</title>");
		sb.append("<style>");
		sb.append(
				"body { font-family: 'Segoe UI', sans-serif; margin: 0; padding: 40px; color: #000; background-color: #e6f7ff; }");
		sb.append(".invoice-box { max-width: 800px; margin: auto; padding: 30px; background: #e6f7ff; }");

		sb.append(
				".header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }");
		sb.append(".header img { max-height: 60px; }");
		sb.append(".header h1 { color: #0056b3; font-size: 28px; margin: 0; }");
		sb.append(".info { display: flex; justify-content: space-between; margin-bottom: 30px; }");
		sb.append(".info div { width: 45%; }");
		sb.append(".section-title { font-weight: bold; color: #0056b3; margin-bottom: 8px; }");
		sb.append("table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }");
		sb.append("th { background-color: #0056b3; color: white; padding: 10px; text-align: left; }");
		sb.append("td { padding: 10px; border-bottom: 1px solid #000; }");
		sb.append(".total-line td { font-weight: bold; background-color: #f1f1f1; }");
		sb.append(".footer { text-align: center; font-size: 14px; color: #888; margin-top: 50px; }");
		sb.append("</style>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<div class='invoice-box'>");

		sb.append("<div class='header' style='margin-bottom: 0;'>");
		sb.append("<table style='width: 100%; border-collapse: collapse; border: none;'>");

		// Fila 1: logo e invoice
		sb.append("<tr style='vertical-align: top; border: none;'>");
		sb.append("<td style='width: 50%; padding-bottom: 5px; border: none;'>");
		sb.append("<img src='data:image/png;base64,").append(logoBase64)
				.append("' alt='Company Logo' style='max-height:80px;'/>");
		sb.append("</td>");
		sb.append("<td style='width: 50%; text-align: right; border: none;'>");
		sb.append("<h1 style='margin: 0; font-size: 26px; color: #0056b3;'>Invoice</h1>");
		sb.append("</td>");
		sb.append("</tr>");

		// Fila 2: información empresa e info factura
		sb.append("<tr style='vertical-align: top; font-size: 14px; border: none;'>");
		sb.append("<td style='padding-top: 0; border: none;'>");
		sb.append("<strong>").append(company.getName()).append("</strong><br/>");
		sb.append(formatAddress(companyAddress)).append("<br/>");
		sb.append(companyAddress.getCity()).append(", ").append(companyAddress.getState()).append(" ")
				.append(companyAddress.getZipCode()).append("<br/>");
		sb.append(company.getEmail()).append("<br/>");
		sb.append(company.getPhone());
		sb.append("</td>");
		sb.append("<td style='text-align: right; padding-top: 0; border: none;'>");
		sb.append("<p style='margin: 0;'>Invoice #: ").append(invoice.getInvoiceNumber()).append("</p>");
		sb.append("<p style='margin: 0;'>Date: ").append(invoice.getDate() != null ? invoice.getDate() : "")
				.append("</p>");
		sb.append("</td>");
		sb.append("</tr>");

		sb.append("</table>");
		sb.append("</div>");

		// Separador real (línea negra discreta)
		sb.append("<div style='border-bottom: 1px solid #000; margin: 10px 0 20px 0;'></div>");

		sb.append("<div style='width: 100%; text-align: right; margin-bottom: 30px;'>");
		sb.append("<div style='font-weight: bold; color: #0056b3; margin-bottom: 8px;'>Bill To:</div>");
		sb.append("<p style='margin: 0;'>");
		sb.append("<strong>").append(customer.getName()).append("</strong><br/>");
		sb.append(customerAddress.getStreet()).append("<br/>");
		sb.append(customerAddress.getCity()).append(", ").append(customerAddress.getState()).append(" ")
				.append(customerAddress.getZipCode()).append("<br/>");
		sb.append(customer.getEmail()).append("<br/>");
		sb.append(customer.getPhone());
		sb.append("</p>");
		sb.append("</div>");

		sb.append("<table style='width: 100%; border-collapse: collapse; margin-bottom: 30px;'>");
		sb.append("<tr><th>Description</th><th>Amount</th></tr>");

		// Ítems
		for (InvoiceItem item : items) {
			sb.append("<tr><td>").append(item.getDescription()).append("</td>");
			sb.append("<td style='text-align: right;'>$").append(item.getAmount()).append("</td></tr>");
		}

		// Agregar filas vacías para dar espacio visual
		int emptyRows = Math.max(0, 3 - items.size());
		for (int i = 0; i < emptyRows; i++) {
			sb.append("<tr><td style='height: 25px;'>&#160;</td><td></td></tr>");
		}

		// Totales — siempre se muestran, pero el valor solo si > 0
		sb.append("<tr><td style='text-align: right; font-weight: bold; border: none;'>Subtotal</td>");
		sb.append("<td style='text-align: right; border: none;'>")
				.append(invoice.getSubtotal() != null && invoice.getSubtotal().doubleValue() > 0
						? "$" + invoice.getSubtotal()
						: "")
				.append("</td></tr>");

		sb.append("<tr><td style='text-align: right; font-weight: bold; border: none;'>Tax</td>");
		sb.append("<td style='text-align: right; border: none;'>")
				.append(invoice.getTaxRate() != null && invoice.getTaxRate().doubleValue() > 0
						? "$" + invoice.getTaxRate()
						: "")
				.append("</td></tr>");

		sb.append("<tr><td style='text-align: right; font-weight: bold; border: none;'>Additional Costs</td>");
		sb.append("<td style='text-align: right; border: none;'>")
				.append(invoice.getAdditionalCosts() != null && invoice.getAdditionalCosts().doubleValue() > 0
						? "$" + invoice.getAdditionalCosts()
						: "")
				.append("</td></tr>");

		// Total final destacado
		sb.append("<tr style='background-color: #0056b3; color: white;'>");
		sb.append("<td style='text-align: right; font-weight: bold;'>Total</td>");
		sb.append("<td style='text-align: right; font-weight: bold;'>$").append(invoice.getTotal())
				.append("</td></tr>");

		sb.append("</table>");

		// Mostrar notas solo si existen y no están vacías
		if (invoice.getNotes() != null && !invoice.getNotes().trim().isEmpty()) {
			sb.append("<div class='section-title'>Notes:</div>");
			sb.append("<p>").append(invoice.getNotes()).append("</p>");
		}

		// Footer agradecimiento estilizado
		sb.append("<div style=\"" + "text-align: center; " + "font-size: 24px; " + "font-family: 'Georgia', serif; "
				+ "font-weight: bold; " + "margin-top: 60px; " + "color: #222;\">");
		sb.append("Thank you for your business");
		sb.append("</div>");

		sb.append("</div>");
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	private static String formatAddress(Address address) {
		if (address == null)
			return "";
		return address.getStreet() + ", " + address.getCity() + ", " + address.getState() + " " + address.getZipCode();
	}
}
