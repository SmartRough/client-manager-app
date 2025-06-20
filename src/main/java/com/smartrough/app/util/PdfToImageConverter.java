package com.smartrough.app.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.io.ByteArrayOutputStream;

public class PdfToImageConverter {

	public static List<String> convertToBase64Images(File pdfFile) throws Exception {
		List<String> base64Images = new ArrayList<>();

		try (PDDocument document = PDDocument.load(pdfFile)) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);

			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				BufferedImage image = pdfRenderer.renderImageWithDPI(page, 150); 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
				base64Images.add(base64);
			}
		}

		return base64Images;
	}
}
