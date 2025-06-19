
package com.smartrough.app.util;

import com.smartrough.app.model.Company;
import com.smartrough.app.model.Contract;
import com.smartrough.app.model.ContractAttachment;
import com.smartrough.app.model.ContractItem;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class ContractHtmlTemplate {

	public static String generateHtml(Contract contract, Company company) {
		StringBuilder sb = new StringBuilder();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		String po = safe(contract.getPoNumber());
		String measureDate = contract.getMeasureDate() != null ? contract.getMeasureDate().format(df) : "";
		String startDate = contract.getStartDate() != null ? contract.getStartDate().format(df) : "";
		String endDate = contract.getEndDate() != null ? contract.getEndDate().format(df) : "";

		sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/>");
		sb.append("<style>");
		sb.append("body { font-family: 'Segoe UI', sans-serif; padding: 30px; background: #fff; color: #000; }");
		sb.append(".contract-box { max-width: 850px; margin: auto; }");
		sb.append(".header, .section, .footer { margin-bottom: 25px; }");
		sb.append(".flex { display: flex; justify-content: space-between; }");
		sb.append(".table { width: 100%; border-collapse: collapse; }");
		sb.append(".table td, .table th { border: 1px solid #ccc; padding: 8px; }");
		sb.append(".signatures td { padding: 40px 10px 10px; text-align: center; }");
		sb.append(".clause-box { width: 65%; }");
		sb.append(".cost-box { width: 30%; }");
		sb.append("img.logo { height: 60px; }");
		sb.append("</style>");
		sb.append("</head><body><div class='contract-box'>");

		// Header
		sb.append("<div class='header flex'>");
		sb.append("<div>");
		sb.append("<img src='file:logo.png' class='logo'/><br/>");
		sb.append("<strong>").append(safe(company.getName())).append("</strong><br/>");
		sb.append("Rep: ").append(safe(company.getRepresentative())).append("<br/>");
		sb.append("Email: ").append(safe(company.getEmail())).append("<br/>");
		sb.append("Phone: ").append(safe(company.getPhone())).append("<br/>");
		sb.append("</div>");
		sb.append("<div>");
		sb.append("<strong>PO Number:</strong> ").append(po).append("<br/>");
		sb.append("<strong>Measure Date:</strong> ").append(measureDate);
		sb.append("</div>");
		sb.append("</div>");

		// Order Info
		sb.append("<div class='section'><h3>Order Form / Sales Agreement</h3>");
		sb.append("<table class='table'>");
		sb.append("<tr><td>Owner 1</td><td>").append(safe(contract.getOwner1())).append("</td>");
		sb.append("<td>Owner 2</td><td>").append(safe(contract.getOwner2())).append("</td></tr>");
		sb.append("<tr><td>Address</td><td colspan='3'>").append(safe(contract.getAddress())).append(", ")
				.append(safe(contract.getCity())).append(", ").append(safe(contract.getState())).append(" ")
				.append(safe(contract.getZip())).append("</td></tr>");
		sb.append("<tr><td>Email</td><td>").append(safe(contract.getEmail())).append("</td>");
		sb.append("<td>Phones</td><td>").append(safe(contract.getHomePhone())).append(" / ")
				.append(safe(contract.getOtherPhone())).append("</td></tr>");
		sb.append("<tr><td>Property Type</td><td colspan='3'>");
		sb.append(contract.isHouse() ? "House " : "").append(contract.isCondo() ? "Condo " : "")
				.append(contract.isMFH() ? "MFH " : "").append(contract.isCommercial() ? "Commercial " : "")
				.append(contract.isHasHOA() ? "(HOA)" : "");
		sb.append("</td></tr>");
		sb.append("</table></div>");

		// Description of Work
		sb.append("<div class='section'><h3>General Description of Work, Materials or Labor</h3><ul>");
		if (contract.getItems() != null) {
			for (ContractItem item : contract.getItems()) {
				sb.append("<li>").append(safe(item.getDescription())).append("</li>");
			}
		}
		sb.append("</ul></div>");

		// Clauses and Cost
		sb.append("<div class='section flex'>");

		sb.append("<div class='clause-box'>");
		sb.append("<h4>Clauses</h4>");
		sb.append(
				"<p>The contractor agrees to perform all work specified above according to standard practices. Any changes must be agreed in writing.</p>");
		sb.append(
				"<p>All materials shall be of standard quality unless otherwise stated. Payment must be made as agreed upon and is subject to approval of the work completed.</p>");
		sb.append("</div>");

		sb.append("<div class='cost-box'>");
		sb.append("<h4>Project Cost</h4>");
		sb.append("<table class='table'>");
		sb.append("<tr><td>Total Price</td><td>$").append(format(contract.getTotalPrice())).append("</td></tr>");
		sb.append("<tr><td>Deposit</td><td>$").append(format(contract.getDeposit())).append("</td></tr>");
		sb.append("<tr><td>Balance Due</td><td>$").append(format(contract.getBalanceDue())).append("</td></tr>");
		sb.append("<tr><td>Amount Financed</td><td>$").append(format(contract.getAmountFinanced()))
				.append("</td></tr>");
		sb.append("<tr><td>Card Info</td><td>").append(safe(contract.getCardType())).append(" ")
				.append(safe(contract.getCardNumber())).append("<br/>").append("Exp: ")
				.append(safe(contract.getCardExp())).append(" / CVC: ").append(safe(contract.getCardCVC()))
				.append("<br/>ZIP: ").append(safe(contract.getCardZip())).append("</td></tr>");
		sb.append("</table></div></div>");

		// Right to Cancel
		sb.append("<div class='section'><h4>Buyer's Right to Cancel</h4>");
		sb.append(
				"<p>The buyer has the right to cancel this agreement within three business days after signing without penalty or obligation by notifying the contractor in writing.</p>");
		sb.append("</div>");

		// Signatures
		sb.append("<div class='section'><table class='table signatures'>");
		sb.append("<tr><td>Contractor Signature</td><td>Owner 1 Signature</td><td>Owner 2 Signature</td></tr>");
		sb.append(
				"<tr><td>__________________________</td><td>__________________________</td><td>__________________________</td></tr>");
		sb.append("</table></div>");

		// Attachments
		if (contract.getAttachments() != null && !contract.getAttachments().isEmpty()) {
			sb.append("<div class='section'><h4>Annexes</h4>");
			int annexIndex = 1;
			for (ContractAttachment a : contract.getAttachments()) {
				String filename = a.getFullFilename();
				String ext = a.getExtension().toLowerCase();
				if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
					String base64 = loadAttachmentImage(contract, a);
					if (base64 != null) {
						sb.append("<h5>Annex ").append((char) ('A' + annexIndex - 1)).append(": ").append(filename)
								.append("</h5>");
						sb.append("<img src='data:image/").append(ext).append(";base64,").append(base64)
								.append("' style='max-width:700px;margin-bottom:30px;'/>");
					}
				} else {
					sb.append("<p>Annex ").append((char) ('A' + annexIndex - 1)).append(": ").append(filename)
							.append("</p>");
				}
				annexIndex++;
			}
			sb.append("</div>");
		}

		sb.append("</div></body></html>");
		return sb.toString();
	}

	private static String loadAttachmentImage(Contract contract, ContractAttachment att) {
		try {
			String folder = contract.getMeasureDate() != null ? contract.getMeasureDate().toString() : "unknown";
			String poSafe = contract.getPoNumber() != null ? contract.getPoNumber().replaceAll("[^a-zA-Z0-9]", "_")
					: "no_po";
			Path path = Path.of(System.getProperty("user.dir"), "contracts", folder, poSafe, att.getFullFilename());

			if (!path.toFile().exists())
				return null;
			return FileSaveHelper.encodeFileToBase64(path.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String format(Double d) {
		return d != null ? String.format("%.2f", d) : "-";
	}

	private static String safe(String s) {
		return s != null ? s : "";
	}
}