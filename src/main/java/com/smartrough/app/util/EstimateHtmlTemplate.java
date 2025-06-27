package com.smartrough.app.util;

import com.smartrough.app.model.Address;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Estimate;
import com.smartrough.app.model.EstimateItem;

import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class EstimateHtmlTemplate {

	public static String generateHtml(Estimate estimate, Company company, Company customer, Address companyAddress,
			Address customerAddress, List<EstimateItem> items) {
		StringBuilder sb = new StringBuilder();

		String formattedDate = estimate.getDate() != null
				? estimate.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
				: "-";

		NumberFormat currencyFormatter = NumberFormat.getNumberInstance(Locale.US);
		currencyFormatter.setMinimumFractionDigits(2);
		currencyFormatter.setMaximumFractionDigits(2);
		String formattedTotal = currencyFormatter.format(estimate.getTotal());

		sb.append("<!DOCTYPE html>");
		sb.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		sb.append("<head><meta charset='UTF-8' />");

		sb.append("<style>");
		sb.append("body { font-family: 'Segoe UI', sans-serif; padding: 30px; background: white; color: #333; }");
		sb.append(
				".estimate-box { background: white; padding: 20px; max-width: 800px; margin: auto; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.05); }");
		sb.append(".header { display: flex; justify-content: space-between; align-items: center; }");
		sb.append(".section { margin-top: 20px; }");
		sb.append(".images { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 15px; }");
		sb.append(".images { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 15px; }");
		sb.append("</style>");

		sb.append("</head><body>");
		sb.append("<div class='estimate-box'>");

		// Header en dos columnas
		sb.append("<table style='width: 100%; border-collapse: collapse;'>");
		sb.append("<tr style='vertical-align: top;'>");

		// Columna izquierda: logo + info empresa
		sb.append("<td style='width: 50%; padding-right: 20px;'>");
		sb.append("<img src='data:image/png;base64,").append(FileSaveHelper.encodeImageToBase64("/img/logo_gcs.png"))
				.append("' alt='Company Logo' style='max-height:100px;'/><br/>");
		sb.append("<strong>").append(company.getName()).append("</strong><br/>");
		sb.append("Representative: ").append(company.getRepresentative()).append("<br/>");
		sb.append(company.getEmail()).append("<br/>");
		sb.append(company.getPhone());
		sb.append("</td>");

		// Columna derecha: título y fecha
		sb.append("<td style='width: 50%; text-align: right;'>");
		sb.append("<h1 style='margin: 0; font-size: 28px; color: #0056b3;'>Work Estimate</h1>");
		sb.append("<p style='margin: 5px 0; font-size: 14px;'>Date: ").append(formattedDate).append("</p>");
		sb.append("</td>");

		sb.append("</tr>");
		sb.append("</table>");

		// Cliente destino alineado a la derecha
		sb.append("<div style='width: 100%; text-align: right; margin-top: 20px;'>");
		sb.append("<div style='font-weight: bold; color: #0056b3;'>Customer:</div>");
		sb.append("<p style='margin: 0;'>");
		sb.append(customer.getName()).append("<br/>");
		sb.append(customerAddress.getStreet()).append("<br/>");
		sb.append(customerAddress.getCity()).append(", ").append(customerAddress.getState()).append(" ")
				.append(customerAddress.getZipCode());
		sb.append("</p>");
		sb.append("</div>");

		// Sección: Descripción centrada
		sb.append("<div style='text-align: center; font-weight: bold; font-size: 18px; margin-top: 40px;'>");
		sb.append("Description of Work");
		sb.append("</div>");

		sb.append("<ul style='margin-top: 10px; padding-left: 40px; font-size: 14px;'>");
		estimate.getItems().stream().sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
				.filter(item -> item.getDescription() != null && !item.getDescription().trim().isEmpty())
				.forEach(item -> sb.append("<li>").append(escapeHtml(item.getDescription())).append("</li>"));

		sb.append("</ul>");

		// Total
		sb.append("<div style='text-align: right; font-size: 16px; margin-top: 30px;'>");
		sb.append("<strong>Total:</strong> $").append(formattedTotal);
		sb.append("</div>");

		if (estimate.getImageNames() != null && !estimate.getImageNames().isEmpty()) {
			sb.append("<div class='section'><strong>Attached Images:</strong><div class='images'>");
			for (String imageName : estimate.getImageNames()) {
				String base64 = loadEstimateImageBase64(estimate, customer, imageName);
				if (base64 != null) {
					System.out.println("Image loaded: " + imageName + " => size: " + base64.length());
					sb.append("<img src='data:image/jpeg;base64,").append(base64).append(
							"' alt='Estimate Image' style='width: 48%; max-height: 200px; object-fit: contain; border: 1px solid #ccc; padding: 4px; border-radius: 4px;'/>");
				}
			}
			sb.append("</div></div>");
		}

		sb.append("<div style='margin-top: 60px;'>");
		sb.append("<table style='width: 100%; margin-top: 30px; font-size: 14px;'>");
		sb.append("<tr>");

		// Firma de la empresa
		sb.append("<td style='width: 50%; text-align: left;'>");
		sb.append("<strong>").append(company.getName()).append("</strong><br/>");
		sb.append("Approved by: ").append(company.getRepresentative() != null ? company.getRepresentative() : "")
				.append("<br/><br/>");
		sb.append("<div style='border-bottom: 1px solid #000; width: 200px; height: 40px;'></div>");
		sb.append("</td>");

		// Firma del cliente
		sb.append("<td style='width: 50%; text-align: right;'>");
		sb.append("<strong>").append(customer.getName()).append("</strong><br/>");
		sb.append("Approved by: ").append(estimate.getApproved_by()).append("<br/><br/>");
		sb.append("<div style='border-bottom: 1px solid #000; width: 200px; height: 40px; float: right;'></div>");
		sb.append("</td>");

		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td></td>"); // columna izquierda vacía
		sb.append("<td style='text-align: right; padding-top: 30px;'>");
		sb.append(
				"<div style='margin-bottom: 20px;'>Date: <span style='display: inline-block; border-bottom: 1px solid #000; width: 160px;'> </span></div>");
		sb.append(
				"<div>PO/CRO: <span style='display: inline-block; border-bottom: 1px solid #000; width: 140px;'> </span></div>");
		sb.append("</td>");
		sb.append("</tr>");

		sb.append("</table>");
		sb.append("</div>");

		sb.append("</div></body></html>");

		return sb.toString();
	}

	private static String loadEstimateImageBase64(Estimate estimate, Company customer, String imageName) {
		try {
			String folder = estimate.getDate().toString(); // yyyy-MM-dd

			// Usar el mismo nombre "seguro" que usaste al guardar las imágenes
			String safeCustomerName = customer.getName().replaceAll("[^a-zA-Z0-9_\\-]", "_");

			String contractName = "estimates_" + estimate.getId();

			Path path = Path.of(System.getProperty("user.dir"), "estimates", folder, safeCustomerName, contractName,
					imageName);

			if (!path.toFile().exists()) {
				System.err.println("⚠️ Archivo no encontrado: " + path);
				return null;
			}

			System.out.println("✅ Leyendo imagen para PDF: " + path);
			return FileSaveHelper.encodeFileToBase64(path.toString());

		} catch (Exception e) {
			System.err.println("❌ Error cargando imagen en EstimateHtmlTemplate: " + imageName);
			e.printStackTrace();
			return null;
		}
	}

	private static String escapeHtml(String input) {
		if (input == null)
			return "";
		return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#x27;");
	}
}
