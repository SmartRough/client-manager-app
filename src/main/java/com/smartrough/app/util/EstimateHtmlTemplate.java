package com.smartrough.app.util;

import com.smartrough.app.model.Address;
import com.smartrough.app.model.Company;
import com.smartrough.app.model.Estimate;
import com.smartrough.app.model.EstimateItem;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EstimateHtmlTemplate {

	public static String generateHtml(Estimate estimate, Company company, Company customer, Address companyAddress,
			Address customerAddress, List<EstimateItem> items) {
		StringBuilder sb = new StringBuilder();

		String formattedDate = estimate.getDate() != null
				? estimate.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
				: "-";

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
				.append("' alt='Company Logo' style='max-height:60px; margin-bottom: 10px;'/><br/>");
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
		for (EstimateItem item : estimate.getItems()) {
			if (item.getDescription() != null && !item.getDescription().trim().isEmpty()) {
				sb.append("<li>").append(escapeHtml(item.getDescription())).append("</li>");
			}
		}
		sb.append("</ul>");

		// Total
		sb.append("<div style='text-align: right; font-size: 16px; margin-top: 30px;'>");
		sb.append("<strong>Total:</strong> $").append(estimate.getTotal());
		sb.append("</div>");

		if (estimate.getImageNames() != null && !estimate.getImageNames().isEmpty()) {
			sb.append("<div class='section'><strong>Attached Images:</strong><div class='images'>");
			for (String imageName : estimate.getImageNames()) {
				String base64 = loadEstimateImageBase64(estimate, customer, imageName);
				if (base64 != null) {
					System.out.println("Image loaded: " + imageName + " => size: " + base64.length());
					sb.append("<img src='data:image/jpeg;base64,").append(base64).append("' alt='Estimate Image' />");
				}
			}
			sb.append("</div></div>");
		}

		sb.append(
				"<div style='text-align: center; font-size: 18px; font-weight: bold; margin-top: 60px; color: #444;'>");
		sb.append("Thank you for considering us.");
		sb.append("</div>");

		sb.append("</div></body></html>");

		return sb.toString();
	}

	private static String loadEstimateImageBase64(Estimate estimate, Company customer, String imageName) {
		try {
			String folder = estimate.getDate().toString(); // yyyy-MM-dd

			// Usar el mismo nombre "seguro" que usaste al guardar las imágenes
			String safeCustomerName = customer.getName().replaceAll("[^a-zA-Z0-9_\\-]", "_");

			Path path = Path.of(System.getProperty("user.dir"), "estimates", folder, safeCustomerName, imageName);

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
