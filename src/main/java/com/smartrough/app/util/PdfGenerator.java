package com.smartrough.app.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.*;

public class PdfGenerator {

	public static File renderHtmlToTempPdf(String htmlContent) throws IOException {
		File tempFile = File.createTempFile("contract_render_", ".pdf");
		tempFile.deleteOnExit();

		try (OutputStream os = new FileOutputStream(tempFile)) {
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.useFastMode();
			builder.withHtmlContent(htmlContent, null);
			builder.toStream(os);
			builder.run();
		}
		return tempFile;
	}

}
