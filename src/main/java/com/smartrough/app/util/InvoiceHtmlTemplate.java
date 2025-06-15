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
				"body { font-family: 'Segoe UI', sans-serif; margin: 0; padding: 40px; color: #333; background-color: #f8f9fa; }");
		sb.append(
				".invoice-box { max-width: 800px; margin: auto; padding: 30px; background: #fff; border: 1px solid #e3e3e3; box-shadow: 0 0 10px rgba(0, 0, 0, 0.05); }");
		sb.append(
				".header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }");
		sb.append(".header img { max-height: 60px; }");
		sb.append(".header h1 { color: #0056b3; font-size: 28px; margin: 0; }");
		sb.append(".info { display: flex; justify-content: space-between; margin-bottom: 30px; }");
		sb.append(".info div { width: 45%; }");
		sb.append(".section-title { font-weight: bold; color: #0056b3; margin-bottom: 8px; }");
		sb.append("table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }");
		sb.append("th { background-color: #0056b3; color: white; padding: 10px; text-align: left; }");
		sb.append("td { padding: 10px; border-bottom: 1px solid #e3e3e3; }");
		sb.append(".total-line td { font-weight: bold; background-color: #f1f1f1; }");
		sb.append(".footer { text-align: center; font-size: 14px; color: #888; margin-top: 50px; }");
		sb.append("</style>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<div class='invoice-box'>");

		sb.append("<div class='header'>");
		sb.append("<img src='data:image/png;base64,").append(logoBase64)
				.append("' alt='Company Logo' style='max-height:60px;'/>");

		sb.append("<h1>Invoice</h1>");
		sb.append("</div>");

		sb.append("<div class='info'>");
		sb.append("<div>");
		sb.append("<div class='section-title'>From:</div>");
		sb.append("<p><strong>").append(company.getName()).append("</strong><br/>")
				.append(formatAddress(companyAddress)).append("<br/>").append(company.getEmail()).append("<br/>")
				.append(company.getPhone()).append("</p>");
		sb.append("</div>");
		sb.append("<div>");
		sb.append("<div class='section-title'>Bill To:</div>");
		sb.append("<p><strong>").append(customer.getName()).append("</strong><br/>")
				.append(formatAddress(customerAddress)).append("<br/>").append(customer.getEmail()).append("<br/>")
				.append(customer.getPhone()).append("</p>");
		sb.append("</div>");
		sb.append("</div>");

		sb.append("<table>");
		sb.append("<tr><th>Description</th><th>Amount</th></tr>");
		for (InvoiceItem item : items) {
			sb.append("<tr><td>").append(item.getDescription()).append("</td>");
			sb.append("<td>").append(item.getAmount()).append("</td></tr>");
		}
		sb.append("<tr class='total-line'><td>Subtotal</td><td>").append(invoice.getSubtotal()).append("</td></tr>");
		sb.append("<tr class='total-line'><td>Tax</td><td>")
				.append(invoice.getTaxRate() != null ? invoice.getTaxRate() : "0.00").append("</td></tr>");
		sb.append("<tr class='total-line'><td>Additional Costs</td><td>")
				.append(invoice.getAdditionalCosts() != null ? invoice.getAdditionalCosts() : "0.00")
				.append("</td></tr>");
		sb.append("<tr class='total-line'><td>Total</td><td>").append(invoice.getTotal()).append("</td></tr>");
		sb.append("</table>");

		sb.append("<div class='section-title'>Notes:</div>");
		sb.append("<p>").append(invoice.getNotes() != null ? invoice.getNotes() : "").append("</p>");

		sb.append("<div class='footer'>Thank you for your business</div>");

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
