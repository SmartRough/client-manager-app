package com.smartrough.app.util;

import com.smartrough.app.model.Company;
import com.smartrough.app.model.Contract;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ContractHtmlTemplate {

	public static String generateHtml(Contract contract, Company company) {
		StringBuilder sb = new StringBuilder();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		String po = safe(contract.getPoNumber());
		String measureDate = contract.getMeasureDate() != null ? contract.getMeasureDate().format(df) : "";
		String startDate = contract.getStartDate() != null ? contract.getStartDate().format(df) : "";
		String endDate = contract.getEndDate() != null ? contract.getEndDate().format(df) : "";

		String logoBase64 = FileSaveHelper.encodeImageToBase64("/img/logo_gcs.png");

		sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/>");
		sb.append("<style>");
		sb.append(
				"body { font-family: Arial, DejaVu Sans, Segoe UI Symbol, 'Noto Sans Symbols', 'Symbola', sans-serif; padding: 30px; background: #fff; color: #000; }");
		sb.append(".contract-box { max-width: 850px; margin: auto; }");
		sb.append(".header, .section, .footer { margin-bottom: 25px; }");
		sb.append(".table { width: 100%; border-collapse: collapse; }");
		sb.append(".table td, .table th { border: 1px solid #ccc; padding: 8px; }");
		sb.append(".signatures td { padding: 40px 10px 10px; text-align: center; }");
		sb.append(".clause-box { width: 65%; }");
		sb.append(".cost-box { width: 30%; }");
		sb.append("img.logo { height: 100px; }");
		sb.append("</style>");
		sb.append("</head><body><div class='contract-box'>");

		// Header
		sb.append("<div class='header'><table style='width: 100%; border: none;'><tr>");

		sb.append("<td style='width: 50%; vertical-align: top;'>");
		if (logoBase64 != null) {
			sb.append("<img src='data:image/png;base64,").append(logoBase64).append("' class='logo'/><br/>");
		}
		sb.append("<strong>").append(safe(company.getName())).append("</strong><br/>");
		sb.append("Phone: ").append(safe(company.getPhone())).append("<br/>");
		sb.append("Email: ").append(safe(company.getEmail())).append("<br/>");
		sb.append("Rep: ").append(safe(company.getRepresentative())).append("<br/>");
		sb.append("Lic. #: ").append(safe(company.getLicense())).append("<br/>");
		sb.append("</td>");

		sb.append("<td style='width: 50%; text-align: right; vertical-align: top;'>");
		sb.append("<strong>PO Number:</strong> ").append(po).append("<br/>");
		sb.append("<strong>Measure Date:</strong> ").append(measureDate);
		sb.append("</td>");

		sb.append("</tr></table></div>");

		// Order Info
		sb.append("<div class='section' style='page-break-inside: avoid;'>");
		sb.append("<h3 style='text-align: center;'>ORDER FORM / SALES AGREEMENT</h3>");
		sb.append("<table style='width: 100%; border-collapse: collapse; font-size: 14px; line-height: 1.6;'>");

		sb.append("<tr>");
		sb.append("<td style='border: 1px solid #ccc;'><strong>Owner #1:</strong> ").append(safe(contract.getOwner1()))
				.append("</td>");
		sb.append("<td style='border: 1px solid #ccc;'><strong>Email:</strong> ").append(safe(contract.getEmail()))
				.append("</td>");
		sb.append("<td style='border: 1px solid #ccc;'>").append(checkboxLine("House:", contract.isHouse()))
				.append("</td>");

		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td style='border: 1px solid #ccc;'><strong>Owner #2:</strong> ").append(safe(contract.getOwner2()))
				.append("</td>");
		sb.append("<td style='border: 1px solid #ccc;'><strong>Date:</strong> ").append(measureDate).append("</td>");
		sb.append("<td style='border: 1px solid #ccc;'>").append(checkboxLine("Condo:", contract.isCondo()))
				.append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td style='border: 1px solid #ccc;'><strong>Address:</strong> ").append(safe(contract.getAddress()))
				.append("</td>");
		sb.append("<td style='border: 1px solid #ccc;'><strong>Home Phone:</strong> ")
				.append(safe(contract.getHomePhone())).append("</td>");
		sb.append("<td style='border: 1px solid #ccc;'>").append(checkboxLine("MFH:", contract.isMFH()))
				.append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td style='border: 1px solid #ccc;'><strong>City:</strong> ").append(safe(contract.getCity()))
				.append(" <strong>State:</strong> ").append(safe(contract.getState())).append(" <strong>Zip:</strong> ")
				.append(safe(contract.getZip())).append("</td>");
		sb.append("<td style='border: 1px solid #ccc;'><strong>Other Phone:</strong> ")
				.append(safe(contract.getOtherPhone())).append("</td>");
		sb.append("<td style='border: 1px solid #ccc;'>").append(checkboxLine("Commercial:", contract.isCommercial()))
				.append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td  colspan='2' style='border: 1px solid #ccc;'></td>");
		sb.append("<td style='border: 1px solid #ccc;'>").append(checkboxLine("HOA:", contract.isHasHOA()))
				.append("</td>");
		sb.append("</tr>");

		sb.append("</table></div>");

		// Description of Work
		sb.append("<div class='section' style='page-break-inside: avoid;'>");
		sb.append("<h3 style='text-align: center;'>General Description of Work, Materials or Labor</h3>");
		sb.append("<p style='text-align: center; font-size: 13px; margin-top: -10px; margin-bottom: 20px;'>");
		sb.append(
				"Plans and Specifications are attached hereto and incorporated herein by reference as Exhibit A (collectively the &quot;Improvement&quot;)");
		sb.append("</p>");

		sb.append("<table style='width: 100%; border-collapse: collapse; font-size: 14px;'>");
		if (contract.getItems() != null) {
			contract.getItems().stream().sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder())).forEach(item -> {
				sb.append("<tr>");
				sb.append("<td style='border-bottom: 1px solid #ccc; padding: 8px;'>")
						.append(safe(item.getDescription())).append("</td>");
				sb.append("</tr>");
			});
		}

		sb.append("</table></div>");

		// Clauses and Cost
		sb.append("<div class='section'>");

		// Título centrado
		sb.append("<h4 style='text-align: center; margin-bottom: 20px;'>Clauses</h4>");

		// Tabla de cláusulas
		sb.append("<table style='width: 100%; border-collapse: collapse; font-size: 13px;'>");

		String intBox = "<div style='width: 40px; height: 25px; border: 1px solid red; text-align: center; font-size: 10px; line-height: 25px; color: red;'>INT.</div>";

		// Clause 1
		sb.append("<tr><td style='width: 60px; vertical-align: top; padding: 8px;'>").append(intBox)
				.append("</td><td style='padding: 8px; border-bottom: 1px solid #ccc; color: red;'>")
				.append("<strong>1.</strong> This Agreement constitutes the complete agreement between the parties and may not be modified except in writing signed by all parties hereto. ORAL REPRESENTATIONS BY EITHER PARTY ARE NOT BINDING AND SHOULD NOT BE RELIED UPON. The Contractor will not provide any services not specifically outlined in this agreement.")
				.append("</td></tr>");

		// Clause 2
		sb.append("<tr><td style='width: 60px; vertical-align: top; padding: 8px;'>").append(intBox)
				.append("</td><td style='padding: 8px; border-bottom: 1px solid #ccc; color: red;'>")
				.append("<strong>2.</strong> Permits. The Owner agrees that the owner shall pay for all permits required for the work. All such costs shall be in addition to the Total price listed in this agreement. The Owner shall be responsible for and shall pay the cost of acquiring HOA approval. It is the Owner's responsibility to be present for any inspections necessary for permitting or HOA purposes.")
				.append("</td></tr>");

		// Clause 3 - with start/end dates
		String range = (startDate.isEmpty() || endDate.isEmpty()) ? "__________________________"
				: startDate + " - " + endDate;
		sb.append("<tr><td style='width: 60px; vertical-align: top; padding: 8px;'>").append(intBox)
				.append("</td><td style='padding: 8px; border-bottom: 1px solid #ccc; color: red;'>")
				.append("<strong>3.</strong> The Commencement date will be between <u>").append(range)
				.append("</u> after approval of measure, the issuance of permits, financing and/or HOA approval.")
				.append("</td></tr>");

		// Clause 4
		sb.append("<tr><td style='width: 60px; vertical-align: top; padding: 8px;'>").append(intBox)
				.append("</td><td style='padding: 8px; border-bottom: 1px solid #ccc; color: red;'>")
				.append("<strong>4.</strong> Lead Safe Paint Practices. The Owner acknowledges receipt of a copy of the \"Renovate Right: Important Lead Hazard Information for families, Child Care providers and schools\" pamphlet. The Owner is aware of the potential risk of lead hazard exposure from renovation activity to be performed in the home.")
				.append("</td></tr>");

		sb.append("</table>");

		// Espacio antes de los costos
		sb.append("<div style='height: 40px;'></div>");

		// Título centrado
		sb.append("<div style='text-align: center; page-break-inside: avoid;'>");
		sb.append("<h4 style='margin-bottom: 20px; font-size: 18px; color: #333;'>Project Cost</h4>");

		// Tabla estilizada
		sb.append(
				"<table style='margin: 0 auto; width: 80%; border-collapse: collapse; font-size: 14px; color: #333; box-shadow: 0 2px 6px rgba(0,0,0,0.1);'>");

		// Encabezado
		sb.append("<thead><tr style='background-color: #1f4e79; color: #fff;'>");
		sb.append("<th style='text-align: center; padding: 12px; border: 1px solid #ddd;'>Description</th>");
		sb.append("<th style='text-align: center; padding: 12px; border: 1px solid #ddd;'>Amount</th>");
		sb.append("</tr></thead>");

		// Cuerpo
		sb.append("<tbody>");
		sb.append("<tr><td style='padding: 12px; border: 1px solid #eee;'>Total Price</td>")
				.append("<td style='padding: 12px; border: 1px solid #eee; text-align: right;'>$")
				.append(format(contract.getTotalPrice())).append("</td></tr>");

		sb.append("<tr><td style='padding: 12px; border: 1px solid #eee;'>Deposit</td>")
				.append("<td style='padding: 12px; border: 1px solid #eee; text-align: right;'>$")
				.append(format(contract.getDeposit())).append("</td></tr>");

		sb.append("<tr><td style='padding: 12px; border: 1px solid #eee;'>Balance Due Upon Substantial Completion</td>")
				.append("<td style='padding: 12px; border: 1px solid #eee; text-align: right;'>$")
				.append(format(contract.getBalanceDue())).append("</td></tr>");

		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</div>");
		// end centered block

		sb.append("</div>"); // end section

		// Right to Cancel
		sb.append("<div class='section'>");

		// Título centrado
		sb.append("<h4 style='text-align: center;'>BUYER'S RIGHT TO CANCEL</h4>");

		// Texto principal legal
		sb.append(
				"<p>This is a home solicitation sale, and if you do not want the goods or services, you may cancel this agreement by providing written notice to the seller in person, by telegram, or by mail. This notice must indicate that you do not want the goods or services and must be delivered or postmarked before midnight of the third business day after you sign this agreement. If you cancel this agreement, the seller may not keep all or part of any cash down payment.</p>");

		// Fecha con negrita si disponible
		if (contract.getMeasureDate() != null) {
			String[] dateParts = contract.getMeasureDate().format(DateTimeFormatter.ofPattern("MMMM dd yyyy"))
					.split(" ");
			String month = dateParts[0];
			String day = dateParts[1];
			String year = dateParts[2];

			sb.append(
					"<p>Executed in Triplicate, one copy of which was delivered and receipt is hereby acknowledged by Buyer, this ")
					.append("<strong>").append(day).append("</strong> day of <strong>").append(month)
					.append("</strong>, <strong>").append(year).append("</strong>.</p>");
		} else {
			sb.append(
					"<p>Executed in Triplicate, one copy of which was delivered and receipt is hereby acknowledged by Buyer, this <strong>______</strong>, day of <strong>_________</strong>, <strong>20____</strong>.</p>");
		}

		// Firma y secciones A-B-C
		sb.append("<p><strong>Approved and Accepted.</strong></p>");
		sb.append("<p><strong>A.</strong> Do not sign this Agreement if blank.<br/>");
		sb.append("<strong>B.</strong> Owner shall retain a copy of this Agreement at the time of signature.<br/>");
		sb.append(
				"<strong>C.</strong> The Owner acknowledges receiving and reading the Additional Terms and Conditions on the reverse side of this page, which terms and conditions are incorporated herein by reference.</p>");

		sb.append("</div>");

		// Signatures
		sb.append("<div class='section'>");

		// Tabla de firmas con 2 columnas (60% y 40%)
		sb.append("<table style='width: 100%; border-collapse: collapse; font-size: 14px;'>");

		// Encabezado: Company Name - Owner
		sb.append("<tr>");
		sb.append("<td style='padding-bottom: 10px;'><strong>").append(safe(company.getName()))
				.append("</strong></td>");
		sb.append("<td style='text-align: right;'><strong>Owner</strong></td>");
		sb.append("</tr>");

		// Firma del contratista y Owner 1
		sb.append("<tr>");
		sb.append("<td style='padding: 10px 0;'>By: ____________________________</td>");
		sb.append("<td style='text-align: right;'>____________________________</td>");
		sb.append("</tr>");

		// Etiquetas centradas debajo de las líneas
		sb.append("<tr>");
		sb.append("<td style='text-align: center;'>(Contractor)</td>");
		sb.append("<td style='text-align: center;'>(Owner #1)</td>");
		sb.append("</tr>");

		// Licencia del contratista y Owner 2
		sb.append("<tr>");
		sb.append("<td style='padding-bottom: 10px; text-align: center;'> State Certified General Contractor - ")
				.append(safe(company.getLicense())).append("</td>");
		sb.append("<td style='text-align: right;'>____________________________</td>");
		sb.append("</tr>");

		// Etiqueta de Owner 2 centrada
		sb.append("<tr>");
		sb.append("<td></td>");
		sb.append("<td style='text-align: center;'>(Owner #2)</td>");
		sb.append("</tr>");

		sb.append("</table>");
		sb.append("</div>");

		sb.append("</div></body></html>");
		return sb.toString();
	}

	// Auxiliares

	private static String checkboxLine(String label, boolean checked) {
		String symbol = checked ? "•" : ""; // punto negro compatible
		return "<table style='border: none; width: 100%;'><tr>"
				+ "<td style='border: none; vertical-align: middle;'><strong>" + label + "</strong></td>"
				+ "<td style='border: 1px solid #000; width: 18px; height: 18px; "
				+ "text-align: center; vertical-align: middle; font-size: 20px; " + // tamaño más grande
				"font-family: Arial, sans-serif; line-height: 18px;'>" + symbol + "</td></tr></table>";
	}

	private static String format(Double d) {
		return d != null ? String.format(Locale.US, "%,.2f", d) : "-";
	}

	private static String safe(String s) {
		return s != null ? s : "";
	}
}
