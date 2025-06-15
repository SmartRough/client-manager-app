package com.smartrough.app.util;

import com.smartrough.app.model.Company;
import com.smartrough.app.model.Estimate;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class EstimateHtmlTemplate {

	public static String generateHtml(Estimate estimate, Company company, Company customer) {
		StringBuilder sb = new StringBuilder();

		String formattedDate = estimate.getDate() != null
				? estimate.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
				: "-";

		sb.append("<!DOCTYPE html>");
		sb.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		sb.append("<head><meta charset='UTF-8' />");

		sb.append("<style>");
		sb.append("body { font-family: 'Segoe UI', sans-serif; padding: 30px; background: #f8f9fa; color: #333; }");
		sb.append(
				".estimate-box { background: white; padding: 20px; max-width: 800px; margin: auto; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.05); }");
		sb.append(".header { display: flex; justify-content: space-between; align-items: center; }");
		sb.append(".section { margin-top: 20px; }");
		sb.append(".images { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 15px; }");
		sb.append(".images img { max-width: 150px; border: 1px solid #ccc; padding: 5px; border-radius: 5px; }");
		sb.append("</style>");

		sb.append("</head><body>");
		sb.append("<div class='estimate-box'>");

		sb.append("<div class='header'>");
		sb.append("<h1>Estimate</h1>");
		sb.append("<div><strong>Date:</strong> ").append(formattedDate).append("</div>");
		sb.append("</div>");

		sb.append("<div class='section'><strong>From:</strong><br/>");
		sb.append(company.getName()).append("<br/>").append(company.getEmail()).append("<br/>")
				.append(company.getPhone()).append("</div>");

		sb.append("<div class='section'><strong>To:</strong><br/>");
		sb.append(customer.getName()).append("<br/>").append(customer.getEmail()).append("<br/>")
				.append(customer.getPhone()).append("</div>");

		sb.append("<div class='section'><strong>Job Description:</strong><br/>");
		sb.append(escapeHtml(estimate.getJobDescription())).append("</div>");

		sb.append("<div class='section'><strong>Total:</strong> $").append(estimate.getTotal()).append("</div>");

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

		sb.append("<div class='section' style='text-align:center;margin-top:30px;color:#888;'>");
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
